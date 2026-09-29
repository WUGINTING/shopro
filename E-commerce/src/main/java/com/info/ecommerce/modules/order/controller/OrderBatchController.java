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
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
public class OrderBatchController {

    private final OrderBatchService orderBatchService;
    private final com.info.ecommerce.modules.auth.service.CurrentUserService currentUserService;

    @PutMapping("/status")
    @Operation(summary = "批量更新訂單狀態", description = "支援批量更新訂單狀態")
    public ApiResponse<List<Long>> batchUpdateStatus(@Valid @RequestBody BatchOrderUpdateDTO dto) {
        if (dto.getTargetStatus() == com.info.ecommerce.modules.order.enums.OrderStatus.PAID
                || dto.getTargetStatus() == com.info.ecommerce.modules.order.enums.OrderStatus.REFUNDED) {
            currentUserService.assertManagerOrAdmin("標記已付款 / 已退款只能由經理或管理員操作");
        }
        return ApiResponse.success("批量更新成功", orderBatchService.batchUpdateStatus(dto));
    }

    @DeleteMapping
    @Operation(summary = "批量刪除訂單")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> batchDeleteOrders(
            @Parameter(description = "訂單 ID 列表") @RequestBody List<Long> orderIds) {
        orderBatchService.batchDeleteOrders(orderIds);
        return ApiResponse.success("批量刪除成功", null);
    }

    @PostMapping("/export")
    @Operation(summary = "導出訂單資料", description = "批量導出訂單資料為 Excel/CSV（返回訂單列表）")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<List<Order>> exportOrders(
            @Parameter(description = "訂單 ID 列表（為空則導出全部）") 
            @RequestBody(required = false) List<Long> orderIds) {
        return ApiResponse.success("訂單資料已準備導出", 
            orderBatchService.exportOrders(orderIds));
    }

    @GetMapping(value = "/export.csv", produces = "text/csv; charset=UTF-8")
    @Operation(summary = "匯出訂單 CSV", description = "依建立日期（含）與狀態篩選；Excel 可直接開啟（UTF-8 BOM）")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public org.springframework.http.ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(required = false) com.info.ecommerce.modules.order.enums.OrderStatus status) {
        byte[] csv = orderBatchService.exportCsv(startDate, endDate, status);
        String filename = "orders-" + java.time.LocalDate.now() + ".csv";
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
