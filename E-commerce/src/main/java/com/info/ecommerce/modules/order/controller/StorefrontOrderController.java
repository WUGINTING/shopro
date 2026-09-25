package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.StorefrontOrderLookupDTO;
import com.info.ecommerce.modules.order.dto.StorefrontPayRequest;
import com.info.ecommerce.modules.order.dto.StorefrontCheckoutRequest;
import com.info.ecommerce.modules.order.dto.StorefrontCheckoutResultDTO;
import com.info.ecommerce.modules.order.dto.StorefrontQuoteDTO;
import com.info.ecommerce.modules.order.service.StorefrontCheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 前台訂單控制器 - 訪客結帳（不需登入）
 */
@RestController
@RequestMapping("/api/storefront/orders")
@RequiredArgsConstructor
@Tag(name = "前台訂單", description = "前台購物車試算、訪客結帳與訂單查詢")
public class StorefrontOrderController {

    private final StorefrontCheckoutService storefrontCheckoutService;
    private final com.info.ecommerce.common.RateLimiter rateLimiter;

    /** 同一來源 1 小時內最多下單數（防止大量假訂單佔用庫存、耗盡優惠券或濫發通知信） */
    @org.springframework.beans.factory.annotation.Value("${app.rate-limit.checkout-per-client:20}")
    private int checkoutPerClient;

    /** 同一 Email 1 小時內最多下單數（每筆訂單都會寄通知信到此信箱） */
    @org.springframework.beans.factory.annotation.Value("${app.rate-limit.checkout-per-email:10}")
    private int checkoutPerEmail;

    /**
     * 以訂單編號 + Email 驗證的端點只計算失敗次數（同一來源 10 分鐘 20 次），避免暴力猜測他人訂單；
     * 成功的查詢（例如付款結果頁輪詢）不計入，共用 IP 的顧客不會互相影響
     */
    private <T> T throttleFailures(HttpServletRequest http, java.util.function.Supplier<T> action) {
        String client = com.info.ecommerce.common.RateLimiter.clientKey(http);
        rateLimiter.check("storefront-order-failure", client, 20, java.time.Duration.ofMinutes(10),
                "查詢失敗次數過多，請稍後再試");
        T result = action.get();
        rateLimiter.release("storefront-order-failure", client);
        return result;
    }

    @PostMapping("/quote")
    @Operation(summary = "結帳試算", description = "依後端商品價格與物流設定計算小計、運費與總額，並檢查庫存")
    public ApiResponse<StorefrontQuoteDTO> quote(@Valid @RequestBody StorefrontCheckoutRequest.QuoteRequest request,
                                                 HttpServletRequest http) {
        boolean hasCoupon = request.getCouponCode() != null && !request.getCouponCode().isBlank();
        String client = com.info.ecommerce.common.RateLimiter.clientKey(http);
        if (hasCoupon) {
            // 無效的優惠券代碼才計次（同一來源 10 分鐘 20 次），避免逐一猜測未公開的優惠券
            rateLimiter.check("coupon-failure", client, 20, java.time.Duration.ofMinutes(10), "優惠券嘗試次數過多，請稍後再試");
        }
        StorefrontQuoteDTO quote = storefrontCheckoutService.quote(request.getItems(), request.getShippingMethod(), request.getCouponCode());
        if (hasCoupon && quote.getCouponMessage() == null) {
            rateLimiter.release("coupon-failure", client);
        }
        return ApiResponse.success(quote);
    }

    @PostMapping("/checkout")
    @Operation(summary = "訪客結帳", description = "建立訂單；付款方式為 ECPAY 時一併回傳綠界付款網址")
    public ApiResponse<StorefrontCheckoutResultDTO> checkout(@Valid @RequestBody StorefrontCheckoutRequest request,
                                                             HttpServletRequest http) {
        String tooMany = "下單次數過多，請稍後再試；如需大量訂購請聯繫客服";
        rateLimiter.check("checkout-client", com.info.ecommerce.common.RateLimiter.clientKey(http), checkoutPerClient,
                java.time.Duration.ofHours(1), tooMany);
        rateLimiter.check("checkout-email", request.getCustomerEmail(), checkoutPerEmail, java.time.Duration.ofHours(1), tooMany);
        return ApiResponse.success("訂單已建立", storefrontCheckoutService.checkout(request));
    }

    @GetMapping("/lookup")
    @Operation(summary = "查詢訂單", description = "以訂單編號與下單時填寫的電子郵件查詢訂單")
    public ApiResponse<StorefrontOrderLookupDTO> lookup(
            @Parameter(description = "訂單編號") @RequestParam String orderNumber,
            @Parameter(description = "下單時填寫的電子郵件") @RequestParam String email,
            HttpServletRequest http) {
        return ApiResponse.success(throttleFailures(http, () -> storefrontCheckoutService.lookupOrder(orderNumber, email)));
    }

    @GetMapping("/shipping-options")
    @Operation(summary = "配送方式與運費", description = "目前開放的配送方式、運費與免運門檻")
    public ApiResponse<java.util.List<java.util.Map<String, Object>>> shippingOptions() {
        return ApiResponse.success(storefrontCheckoutService.shippingOptions());
    }

    @GetMapping("/payment-options")
    @Operation(summary = "付款方式", description = "目前可用的付款方式（線上付款依後台金流設定的啟用 / 維護狀態）")
    public ApiResponse<java.util.List<java.util.Map<String, Object>>> paymentOptions() {
        return ApiResponse.success(storefrontCheckoutService.paymentOptions());
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消訂單", description = "待付款且尚未出貨的訂單可由顧客自行取消（以訂單編號 + 下單 Email 驗證）")
    public ApiResponse<StorefrontOrderLookupDTO> cancel(@Valid @RequestBody StorefrontPayRequest request, HttpServletRequest http) {
        return ApiResponse.success("訂單已取消", throttleFailures(http,
                () -> storefrontCheckoutService.cancelOrder(request.getOrderNumber(), request.getEmail())));
    }

    @PostMapping("/pay")
    @Operation(summary = "重新付款", description = "待付款的線上付款訂單重新取得綠界付款網址（以訂單編號 + 下單 Email 驗證）")
    public ApiResponse<StorefrontCheckoutResultDTO> pay(@Valid @RequestBody StorefrontPayRequest request, HttpServletRequest http) {
        return ApiResponse.success(throttleFailures(http, () -> storefrontCheckoutService.payAgain(
                request.getOrderNumber(), request.getEmail(), "ADMIN_STORE".equalsIgnoreCase(request.getChannel()))));
    }
}
