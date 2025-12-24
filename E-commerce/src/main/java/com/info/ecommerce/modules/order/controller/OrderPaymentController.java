package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderPaymentDTO;
import com.info.ecommerce.modules.order.enums.PaymentStatus;
import com.info.ecommerce.modules.order.service.OrderPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 付款控制器 - 金流管理
 */
@RestController
@RequestMapping("/api/orders/payments")
@RequiredArgsConstructor
@Tag(name = "訂單付款", description = "跟蹤付款狀態（待付款、已付款、退款中、已退款）")
public class OrderPaymentController {

    private final OrderPaymentService orderPaymentService;

    @PostMapping
    @Operation(summary = "創建付款記錄")
    public ApiResponse<OrderPaymentDTO> createPayment(@Valid @RequestBody OrderPaymentDTO dto) {
        return ApiResponse.success("付款記錄已建立", orderPaymentService.createPayment(dto));
    }

    @PatchMapping("/{paymentId}/status")
    @Operation(summary = "更新付款狀態")
    public ApiResponse<OrderPaymentDTO> updatePaymentStatus(
            @Parameter(description = "付款記錄 ID") @PathVariable Long paymentId,
            @Parameter(description = "付款狀態") @RequestParam PaymentStatus status) {
        return ApiResponse.success("付款狀態已更新", 
            orderPaymentService.updatePaymentStatus(paymentId, status));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "取得訂單的付款記錄")
    public ApiResponse<List<OrderPaymentDTO>> getPaymentsByOrderId(
            @Parameter(description = "訂單 ID") @PathVariable Long orderId) {
        return ApiResponse.success(orderPaymentService.getPaymentsByOrderId(orderId));
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "申請退款")
    public ApiResponse<OrderPaymentDTO> requestRefund(
            @Parameter(description = "付款記錄 ID") @PathVariable Long paymentId,
            @Parameter(description = "退款金額") @RequestParam BigDecimal refundAmount) {
        return ApiResponse.success("退款申請已提交", 
            orderPaymentService.requestRefund(paymentId, refundAmount));
    }
}
