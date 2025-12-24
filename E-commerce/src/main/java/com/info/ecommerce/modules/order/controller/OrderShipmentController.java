package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderShipmentDTO;
import com.info.ecommerce.modules.order.enums.ShippingStatus;
import com.info.ecommerce.modules.order.service.OrderShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流控制器 - 物流管理
 */
@RestController
@RequestMapping("/api/orders/shipments")
@RequiredArgsConstructor
@Tag(name = "訂單物流", description = "訂單出貨單的建立與跟蹤物流號碼")
public class OrderShipmentController {

    private final OrderShipmentService orderShipmentService;

    @PostMapping
    @Operation(summary = "創建物流記錄", description = "建立訂單出貨單")
    public ApiResponse<OrderShipmentDTO> createShipment(@Valid @RequestBody OrderShipmentDTO dto) {
        return ApiResponse.success("物流記錄已建立", orderShipmentService.createShipment(dto));
    }

    @PatchMapping("/{shipmentId}/status")
    @Operation(summary = "更新物流狀態")
    public ApiResponse<OrderShipmentDTO> updateShippingStatus(
            @Parameter(description = "物流記錄 ID") @PathVariable Long shipmentId,
            @Parameter(description = "物流狀態") @RequestParam ShippingStatus status) {
        return ApiResponse.success("物流狀態已更新", 
            orderShipmentService.updateShippingStatus(shipmentId, status));
    }

    @PatchMapping("/{shipmentId}/tracking")
    @Operation(summary = "更新物流單號", description = "跟蹤物流號碼")
    public ApiResponse<OrderShipmentDTO> updateTrackingNumber(
            @Parameter(description = "物流記錄 ID") @PathVariable Long shipmentId,
            @Parameter(description = "物流單號") @RequestParam String trackingNumber) {
        return ApiResponse.success("物流單號已更新", 
            orderShipmentService.updateTrackingNumber(shipmentId, trackingNumber));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "取得訂單的物流記錄")
    public ApiResponse<List<OrderShipmentDTO>> getShipmentsByOrderId(
            @Parameter(description = "訂單 ID") @PathVariable Long orderId) {
        return ApiResponse.success(orderShipmentService.getShipmentsByOrderId(orderId));
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "根據物流單號查詢")
    public ApiResponse<OrderShipmentDTO> findByTrackingNumber(
            @Parameter(description = "物流單號") @PathVariable String trackingNumber) {
        return ApiResponse.success(orderShipmentService.findByTrackingNumber(trackingNumber));
    }
}
