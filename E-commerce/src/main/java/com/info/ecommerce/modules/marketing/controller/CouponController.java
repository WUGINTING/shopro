package com.info.ecommerce.modules.marketing.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.marketing.dto.CouponDTO;
import com.info.ecommerce.modules.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * 優惠券管理（後台員工）。顧客在結帳時輸入代碼，由 /api/storefront/orders/quote 試算套用。
 */
@RestController
@RequestMapping("/api/marketing/coupons")
@RequiredArgsConstructor
@Tag(name = "優惠券管理", description = "優惠券（折扣碼）管理")
public class CouponController {

    private final CouponService couponService;

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    @Operation(summary = "建立優惠券")
    public ApiResponse<CouponDTO> create(@Valid @RequestBody CouponDTO dto) {
        return ApiResponse.success("優惠券已建立", couponService.create(dto));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "更新優惠券")
    public ApiResponse<CouponDTO> update(@PathVariable Long id, @Valid @RequestBody CouponDTO dto) {
        return ApiResponse.success("優惠券已更新", couponService.update(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得優惠券")
    public ApiResponse<CouponDTO> get(@PathVariable Long id) {
        return ApiResponse.success(couponService.get(id));
    }

    @GetMapping
    @Operation(summary = "分頁查詢優惠券")
    public ApiResponse<Page<CouponDTO>> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(couponService.list(
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除優惠券（未被使用過才可刪除）")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return ApiResponse.success("優惠券已刪除", null);
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}/enable")
    @Operation(summary = "啟用優惠券")
    public ApiResponse<CouponDTO> enable(@PathVariable Long id) {
        return ApiResponse.success(couponService.setEnabled(id, true));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}/disable")
    @Operation(summary = "停用優惠券")
    public ApiResponse<CouponDTO> disable(@PathVariable Long id) {
        return ApiResponse.success(couponService.setEnabled(id, false));
    }
}
