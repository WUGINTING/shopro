package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.auth.service.CurrentUserService;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.dto.OrderRefundRequest;
import com.info.ecommerce.modules.order.service.OrderRefundService;
import com.info.ecommerce.modules.order.service.OrderService;
import com.info.ecommerce.modules.order.service.OrderStatusRules;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 訂單控制器 - 基礎 CRUD 操作
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "訂單管理", description = "訂單 CRUD 及基本管理功能")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;
    private final OrderRefundService orderRefundService;

    @PostMapping
    @Operation(summary = "創建訂單", description = "後台手動建立新訂單（顧客下單請使用 /api/storefront/orders/checkout，價格由後端計算）")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<OrderDTO> createOrder(@Valid @RequestBody OrderDTO dto) {
        return ApiResponse.success("訂單已建立", orderService.createOrder(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新訂單", description = "更新訂單資料")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<OrderDTO> updateOrder(
            @Parameter(description = "訂單 ID") @PathVariable Long id,
            @Valid @RequestBody OrderDTO dto) {
        if (dto.getStatus() == OrderStatus.PAID || dto.getStatus() == OrderStatus.REFUNDED) {
            currentUserService.assertManagerOrAdmin("標記已付款 / 已退款只能由經理或管理員操作");
        }
        return ApiResponse.success("訂單已更新", orderService.updateOrder(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得訂單詳情", description = "根據訂單 ID 查詢訂單詳細資料")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER')")
    public ApiResponse<OrderDTO> getOrder(
            @Parameter(description = "訂單 ID") @PathVariable Long id) {
        OrderDTO order = orderService.getOrder(id);
        currentUserService.assertCanAccessOrder(order.getCustomerId(), order.getCustomerEmail());
        return ApiResponse.success(order);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除訂單", description = "單一訂單刪除")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteOrder(
            @Parameter(description = "訂單 ID") @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success("訂單已刪除", null);
    }

//    @DeleteMapping("/batch")
//    @Operation(summary = "批量刪除訂單", description = "一次刪除多筆訂單")
//    public ApiResponse<Void> batchDeleteOrders(
//            @Parameter(description = "訂單 ID 列表") @RequestBody List<Long> ids) {
//        orderService.deleteOrders(ids);
//        return ApiResponse.success("訂單已批量刪除", null);
//    }

    @GetMapping
    @Operation(summary = "分頁查詢訂單", description = "查詢所有訂單（分頁）")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<Page<OrderDTO>> listOrders(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listOrders(pageable));
    }

    @GetMapping("/my")
    @Operation(summary = "取得我的訂單", description = "取得當前登入用戶的訂單列表（CUSTOMER 專用）")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Page<OrderDTO>> getMyOrders(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        // 會員與 CRM 會員資料以 Email 對應；尚未下過單的會員沒有 CRM 資料，回傳空清單
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(currentUserService.currentMember()
                .map(Member::getId)
                .map(customerId -> orderService.listOrdersByCustomerId(customerId, pageable))
                .orElse(Page.empty(pageable)));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "根據客戶 ID 查詢訂單")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'CUSTOMER')")
    public ApiResponse<Page<OrderDTO>> listOrdersByCustomer(
            @Parameter(description = "客戶 ID") @PathVariable Long customerId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        currentUserService.assertCanAccessCustomer(customerId);
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listOrdersByCustomerId(customerId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根據狀態查詢訂單")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<Page<OrderDTO>> listOrdersByStatus(
            @Parameter(description = "訂單狀態") @PathVariable OrderStatus status,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listOrdersByStatus(status, pageable));
    }

    @GetMapping("/draft")
    @Operation(summary = "查詢暫存訂單", description = "查詢所有暫存的未完成訂單")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<Page<OrderDTO>> listDraftOrders(
            @Parameter(description = "是否暫存") @RequestParam(defaultValue = "true") Boolean isDraft,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listDraftOrders(isDraft, pageable));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新訂單狀態", description = "允許使用者更新訂單狀態")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<OrderDTO> updateOrderStatus(
            @Parameter(description = "訂單 ID") @PathVariable Long id,
            @Parameter(description = "新狀態") @RequestParam OrderStatus status,
            @Parameter(description = "操作者 ID") @RequestParam(required = false) Long operatorId,
            @Parameter(description = "操作者名稱") @RequestParam(required = false) String operatorName) {
        // 手動標記已付款（未經金流）會觸發付款通知與會員累積消費，限經理或管理員
        if (status == OrderStatus.PAID || status == OrderStatus.REFUNDED) {
            currentUserService.assertManagerOrAdmin("標記已付款 / 已退款只能由經理或管理員操作");
        }
        return ApiResponse.success("訂單狀態已更新",
            orderService.updateOrderStatus(id, status, operatorId, operatorName));
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "登記退款", description = "於綠界後台或以匯款完成退款後登記；全額退款時訂單改為已退款並寄送通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<OrderDTO> refund(@Parameter(description = "訂單 ID") @PathVariable Long id,
                                        @Valid @RequestBody OrderRefundRequest request) {
        var operator = currentUserService.currentUser();
        return ApiResponse.success("退款已登記", orderRefundService.refund(id, request,
                operator.map(user -> user.getId()).orElse(null),
                operator.map(user -> user.getUsername()).orElse(null)));
    }

    @GetMapping("/status-transitions")
    @Operation(summary = "訂單狀態可變更方向", description = "各狀態可以改成哪些狀態（後台下拉選單使用）")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<java.util.Map<OrderStatus, java.util.Set<OrderStatus>>> statusTransitions() {
        java.util.Map<OrderStatus, java.util.Set<OrderStatus>> transitions = new java.util.EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            transitions.put(status, OrderStatusRules.nextStatuses(status));
        }
        return ApiResponse.success(transitions);
    }
}
