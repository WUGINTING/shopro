package com.info.ecommerce.modules.dashboard.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.dashboard.dto.DashboardStatsDTO;
import com.info.ecommerce.modules.dashboard.dto.RecentOrderDTO;
import com.info.ecommerce.modules.dashboard.dto.TopProductDTO;
import com.info.ecommerce.modules.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dashboard Controller - Provides summary statistics for the admin dashboard
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "儀表板", description = "後台儀表板統計數據")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "取得儀表板統計摘要", description = "包含商品、訂單、客戶和銷售額統計")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<DashboardStatsDTO> getStats() {
        return ApiResponse.success(dashboardService.getStats());
    }

    @GetMapping("/recent-orders")
    @Operation(summary = "取得最近訂單", description = "取得最近的訂單列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<List<RecentOrderDTO>> getRecentOrders(
            @Parameter(description = "限制數量") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(dashboardService.getRecentOrders(limit));
    }

    @GetMapping("/top-products")
    @Operation(summary = "取得熱銷商品", description = "取得銷量最高的商品列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<List<TopProductDTO>> getTopProducts(
            @Parameter(description = "限制數量") @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(dashboardService.getTopProducts(limit));
    }
}
