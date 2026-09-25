package com.info.ecommerce.modules.marketing.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.marketing.dto.CouponDTO;
import com.info.ecommerce.modules.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台公開優惠券（後台設定為「公開顯示」且目前可使用者）
 */
@RestController
@RequestMapping("/api/storefront/coupons")
@RequiredArgsConstructor
@Tag(name = "前台優惠券", description = "顧客可見的公開優惠券")
public class StorefrontCouponController {

    private final CouponService couponService;

    @GetMapping
    @Operation(summary = "公開且可使用的優惠券")
    public ApiResponse<List<CouponDTO>> list() {
        return ApiResponse.success(couponService.listPublic());
    }
}
