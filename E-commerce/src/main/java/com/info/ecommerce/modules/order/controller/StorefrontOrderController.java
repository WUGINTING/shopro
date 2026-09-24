package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.StorefrontCheckoutRequest;
import com.info.ecommerce.modules.order.dto.StorefrontCheckoutResultDTO;
import com.info.ecommerce.modules.order.dto.StorefrontQuoteDTO;
import com.info.ecommerce.modules.order.service.StorefrontCheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping("/quote")
    @Operation(summary = "結帳試算", description = "依後端商品價格與物流設定計算小計、運費與總額，並檢查庫存")
    public ApiResponse<StorefrontQuoteDTO> quote(@Valid @RequestBody StorefrontCheckoutRequest.QuoteRequest request) {
        return ApiResponse.success(storefrontCheckoutService.quote(request.getItems(), request.getShippingMethod()));
    }

    @PostMapping("/checkout")
    @Operation(summary = "訪客結帳", description = "建立訂單；付款方式為 ECPAY 時一併回傳綠界付款網址")
    public ApiResponse<StorefrontCheckoutResultDTO> checkout(@Valid @RequestBody StorefrontCheckoutRequest request) {
        return ApiResponse.success("訂單已建立", storefrontCheckoutService.checkout(request));
    }

    @GetMapping("/lookup")
    @Operation(summary = "查詢訂單", description = "以訂單編號與下單時填寫的電子郵件查詢訂單")
    public ApiResponse<OrderDTO> lookup(
            @Parameter(description = "訂單編號") @RequestParam String orderNumber,
            @Parameter(description = "下單時填寫的電子郵件") @RequestParam String email) {
        return ApiResponse.success(storefrontCheckoutService.lookupOrder(orderNumber, email));
    }
}
