package com.info.ecommerce.modules.dashboard.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.dashboard.dto.DashboardOverviewDTO;
import com.info.ecommerce.modules.dashboard.dto.DashboardStatsDTO;
import com.info.ecommerce.modules.dashboard.dto.RecentOrderDTO;
import com.info.ecommerce.modules.dashboard.dto.TopProductDTO;
import com.info.ecommerce.modules.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Admin dashboard summary endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<DashboardStatsDTO> getStats() {
        return ApiResponse.success(dashboardService.getStats());
    }

    @GetMapping("/recent-orders")
    @Operation(summary = "Get recent orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<List<RecentOrderDTO>> getRecentOrders(
            @Parameter(description = "Max number of records") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(dashboardService.getRecentOrders(limit));
    }

    @GetMapping("/top-products")
    @Operation(summary = "Get top products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<List<TopProductDTO>> getTopProducts(
            @Parameter(description = "Max number of records") @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(dashboardService.getTopProducts(limit));
    }

    @GetMapping("/overview")
    @Operation(summary = "Get unified admin dashboard overview",
            description = "Returns stats, recent orders, top products, lookups, and shortcut metadata in one request")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ApiResponse<DashboardOverviewDTO> getOverview(
            @RequestParam(defaultValue = "8") int recentOrderLimit,
            @RequestParam(defaultValue = "5") int topProductLimit) {
        return ApiResponse.success(dashboardService.getOverview(recentOrderLimit, topProductLimit));
    }
}
