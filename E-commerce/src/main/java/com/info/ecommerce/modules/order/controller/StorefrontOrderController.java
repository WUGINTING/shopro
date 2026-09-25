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

    /** 以訂單編號 + Email 驗證的端點限制頻率，避免暴力猜測他人訂單 */
    private void throttleOrderAccess(HttpServletRequest http) {
        rateLimiter.check("storefront-order", http.getRemoteAddr(), 30, java.time.Duration.ofMinutes(10),
                "查詢次數過多，請稍後再試");
    }

    @PostMapping("/quote")
    @Operation(summary = "結帳試算", description = "依後端商品價格與物流設定計算小計、運費與總額，並檢查庫存")
    public ApiResponse<StorefrontQuoteDTO> quote(@Valid @RequestBody StorefrontCheckoutRequest.QuoteRequest request) {
        return ApiResponse.success(storefrontCheckoutService.quote(request.getItems(), request.getShippingMethod(), request.getCouponCode()));
    }

    @PostMapping("/checkout")
    @Operation(summary = "訪客結帳", description = "建立訂單；付款方式為 ECPAY 時一併回傳綠界付款網址")
    public ApiResponse<StorefrontCheckoutResultDTO> checkout(@Valid @RequestBody StorefrontCheckoutRequest request) {
        return ApiResponse.success("訂單已建立", storefrontCheckoutService.checkout(request));
    }

    @GetMapping("/lookup")
    @Operation(summary = "查詢訂單", description = "以訂單編號與下單時填寫的電子郵件查詢訂單")
    public ApiResponse<StorefrontOrderLookupDTO> lookup(
            @Parameter(description = "訂單編號") @RequestParam String orderNumber,
            @Parameter(description = "下單時填寫的電子郵件") @RequestParam String email,
            HttpServletRequest http) {
        throttleOrderAccess(http);
        return ApiResponse.success(storefrontCheckoutService.lookupOrder(orderNumber, email));
    }

    @GetMapping("/shipping-options")
    @Operation(summary = "配送方式與運費", description = "目前開放的配送方式、運費與免運門檻")
    public ApiResponse<java.util.List<java.util.Map<String, Object>>> shippingOptions() {
        return ApiResponse.success(storefrontCheckoutService.shippingOptions());
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消訂單", description = "待付款且尚未出貨的訂單可由顧客自行取消（以訂單編號 + 下單 Email 驗證）")
    public ApiResponse<StorefrontOrderLookupDTO> cancel(@Valid @RequestBody StorefrontPayRequest request, HttpServletRequest http) {
        throttleOrderAccess(http);
        return ApiResponse.success("訂單已取消", storefrontCheckoutService.cancelOrder(request.getOrderNumber(), request.getEmail()));
    }

    @PostMapping("/pay")
    @Operation(summary = "重新付款", description = "待付款的線上付款訂單重新取得綠界付款網址（以訂單編號 + 下單 Email 驗證）")
    public ApiResponse<StorefrontCheckoutResultDTO> pay(@Valid @RequestBody StorefrontPayRequest request, HttpServletRequest http) {
        throttleOrderAccess(http);
        return ApiResponse.success(storefrontCheckoutService.payAgain(request.getOrderNumber(), request.getEmail(),
                "ADMIN_STORE".equalsIgnoreCase(request.getChannel())));
    }
}
