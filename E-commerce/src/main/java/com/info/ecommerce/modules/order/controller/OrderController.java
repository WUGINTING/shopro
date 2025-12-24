package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 訂單控制器 - 基礎 CRUD 操作
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "訂單管理", description = "訂單 CRUD 及基本管理功能")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "創建訂單", description = "手動建立新訂單，包含選擇客戶、商品、金額")
    public ApiResponse<OrderDTO> createOrder(@Valid @RequestBody OrderDTO dto) {
        return ApiResponse.success("訂單已建立", orderService.createOrder(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新訂單", description = "更新訂單資料")
    public ApiResponse<OrderDTO> updateOrder(
            @Parameter(description = "訂單 ID") @PathVariable Long id,
            @Valid @RequestBody OrderDTO dto) {
        return ApiResponse.success("訂單已更新", orderService.updateOrder(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得訂單詳情", description = "根據訂單 ID 查詢訂單詳細資料")
    public ApiResponse<OrderDTO> getOrder(
            @Parameter(description = "訂單 ID") @PathVariable Long id) {
        return ApiResponse.success(orderService.getOrder(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除訂單", description = "單一訂單刪除")
    public ApiResponse<Void> deleteOrder(
            @Parameter(description = "訂單 ID") @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success("訂單已刪除", null);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量刪除訂單", description = "一次刪除多筆訂單")
    public ApiResponse<Void> batchDeleteOrders(
            @Parameter(description = "訂單 ID 列表") @RequestBody List<Long> ids) {
        orderService.deleteOrders(ids);
        return ApiResponse.success("訂單已批量刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢訂單", description = "查詢所有訂單（分頁）")
    public ApiResponse<Page<OrderDTO>> listOrders(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listOrders(pageable));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "根據客戶 ID 查詢訂單")
    public ApiResponse<Page<OrderDTO>> listOrdersByCustomer(
            @Parameter(description = "客戶 ID") @PathVariable Long customerId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listOrdersByCustomerId(customerId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根據狀態查詢訂單")
    public ApiResponse<Page<OrderDTO>> listOrdersByStatus(
            @Parameter(description = "訂單狀態") @PathVariable OrderStatus status,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listOrdersByStatus(status, pageable));
    }

    @GetMapping("/draft")
    @Operation(summary = "查詢暫存訂單", description = "查詢所有暫存的未完成訂單")
    public ApiResponse<Page<OrderDTO>> listDraftOrders(
            @Parameter(description = "是否暫存") @RequestParam(defaultValue = "true") Boolean isDraft,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listDraftOrders(isDraft, pageable));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新訂單狀態", description = "允許使用者更新訂單狀態")
    public ApiResponse<OrderDTO> updateOrderStatus(
            @Parameter(description = "訂單 ID") @PathVariable Long id,
            @Parameter(description = "新狀態") @RequestParam OrderStatus status,
            @Parameter(description = "操作者 ID") @RequestParam(required = false) Long operatorId,
            @Parameter(description = "操作者名稱") @RequestParam(required = false) String operatorName) {
        return ApiResponse.success("訂單狀態已更新", 
            orderService.updateOrderStatus(id, status, operatorId, operatorName));
    }
}
