package com.info.ecommerce.modules.order.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.order.dto.OrderStatisticsDTO;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.service.OrderStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 訂單統計控制器 - 數據儀表板
 */
@RestController
@RequestMapping("/api/orders/statistics")
@RequiredArgsConstructor
@Tag(name = "訂單統計", description = "訂單統計與數據儀表板功能")
public class OrderStatisticsController {

    private final OrderStatisticsService orderStatisticsService;

    @GetMapping
    @Operation(summary = "取得訂單統計資料", 
               description = "包含每日訂單趨勢、金額分布、狀態占比的可視化統計")
    public ApiResponse<OrderStatisticsDTO> getStatistics(
            @Parameter(description = "開始日期") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "結束日期") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ApiResponse.success(orderStatisticsService.getStatistics(startDate, endDate));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根據狀態取得統計")
    public ApiResponse<Map<String, Object>> getStatisticsByStatus(
            @Parameter(description = "訂單狀態") @PathVariable OrderStatus status) {
        return ApiResponse.success(orderStatisticsService.getStatisticsByStatus(status));
    }

    @GetMapping("/today")
    @Operation(summary = "取得今日訂單統計")
    public ApiResponse<OrderStatisticsDTO> getTodayStatistics() {
        return ApiResponse.success(orderStatisticsService.getTodayStatistics());
    }

    @GetMapping("/week")
    @Operation(summary = "取得本週訂單統計")
    public ApiResponse<OrderStatisticsDTO> getWeekStatistics() {
        return ApiResponse.success(orderStatisticsService.getWeekStatistics());
    }

    @GetMapping("/month")
    @Operation(summary = "取得本月訂單統計")
    public ApiResponse<OrderStatisticsDTO> getMonthStatistics() {
        return ApiResponse.success(orderStatisticsService.getMonthStatistics());
    }
}
