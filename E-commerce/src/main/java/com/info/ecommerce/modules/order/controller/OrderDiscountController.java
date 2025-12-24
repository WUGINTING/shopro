package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderDiscountDTO;
import com.info.ecommerce.modules.order.service.OrderDiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 訂單折扣控制器 - 折扣管理
 */
@RestController
@RequestMapping("/api/orders/discounts")
@RequiredArgsConstructor
@Tag(name = "訂單折扣", description = "提供即時折扣的操作")
public class OrderDiscountController {

    private final OrderDiscountService orderDiscountService;

    @PostMapping
    @Operation(summary = "新增訂單折扣", description = "為訂單添加折扣")
    public ApiResponse<OrderDiscountDTO> addDiscount(@Valid @RequestBody OrderDiscountDTO dto) {
        return ApiResponse.success("折扣已新增", orderDiscountService.addDiscount(dto));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "取得訂單的所有折扣")
    public ApiResponse<List<OrderDiscountDTO>> getDiscountsByOrderId(
            @Parameter(description = "訂單 ID") @PathVariable Long orderId) {
        return ApiResponse.success(orderDiscountService.getDiscountsByOrderId(orderId));
    }

    @GetMapping("/code/{discountCode}")
    @Operation(summary = "根據折扣代碼查詢")
    public ApiResponse<List<OrderDiscountDTO>> findByDiscountCode(
            @Parameter(description = "折扣代碼") @PathVariable String discountCode) {
        return ApiResponse.success(orderDiscountService.findByDiscountCode(discountCode));
    }

    @DeleteMapping("/{discountId}")
    @Operation(summary = "刪除訂單折扣")
    public ApiResponse<Void> deleteDiscount(
            @Parameter(description = "折扣記錄 ID") @PathVariable Long discountId) {
        orderDiscountService.deleteDiscount(discountId);
        return ApiResponse.success("折扣已刪除", null);
    }
}
