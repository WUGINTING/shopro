package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderHistoryDTO;
import com.info.ecommerce.modules.order.service.OrderHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 訂單歷史控制器
 */
@RestController
@RequestMapping("/api/orders/history")
@RequiredArgsConstructor
@Tag(name = "訂單歷史", description = "儲存所有訂單操作紀錄")
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "取得訂單歷史記錄", description = "查看訂單的所有操作記錄")
    public ApiResponse<List<OrderHistoryDTO>> getOrderHistory(
            @Parameter(description = "訂單 ID") @PathVariable Long orderId) {
        return ApiResponse.success(orderHistoryService.getOrderHistory(orderId));
    }

    @GetMapping("/order/{orderId}/page")
    @Operation(summary = "分頁取得訂單歷史記錄")
    public ApiResponse<Page<OrderHistoryDTO>> getOrderHistoryPage(
            @Parameter(description = "訂單 ID") @PathVariable Long orderId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderHistoryService.getOrderHistoryPage(orderId, pageable));
    }
}
