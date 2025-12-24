package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.BatchOrderUpdateDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.service.OrderBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 訂單批次操作控制器
 */
@RestController
@RequestMapping("/api/orders/batch")
@RequiredArgsConstructor
@Tag(name = "訂單批次操作", description = "批量更新訂單狀態及批量導出訂單資料")
public class OrderBatchController {

    private final OrderBatchService orderBatchService;

    @PutMapping("/status")
    @Operation(summary = "批量更新訂單狀態", description = "支援批量更新訂單狀態")
    public ApiResponse<List<Long>> batchUpdateStatus(@Valid @RequestBody BatchOrderUpdateDTO dto) {
        return ApiResponse.success("批量更新成功", orderBatchService.batchUpdateStatus(dto));
    }

    @DeleteMapping
    @Operation(summary = "批量刪除訂單")
    public ApiResponse<Void> batchDeleteOrders(
            @Parameter(description = "訂單 ID 列表") @RequestBody List<Long> orderIds) {
        orderBatchService.batchDeleteOrders(orderIds);
        return ApiResponse.success("批量刪除成功", null);
    }

    @PostMapping("/export")
    @Operation(summary = "導出訂單資料", description = "批量導出訂單資料為 Excel/CSV（返回訂單列表）")
    public ApiResponse<List<Order>> exportOrders(
            @Parameter(description = "訂單 ID 列表（為空則導出全部）") 
            @RequestBody(required = false) List<Long> orderIds) {
        return ApiResponse.success("訂單資料已準備導出", 
            orderBatchService.exportOrders(orderIds));
    }
}
