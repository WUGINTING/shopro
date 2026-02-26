package com.info.ecommerce.modules.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Unified admin dashboard overview payload")
public class DashboardOverviewDTO {

    private DashboardStatsDTO stats;
    private List<RecentOrderDTO> recentOrders;
    private List<TopProductDTO> topProducts;
    private DashboardLookupsDTO lookups;
    private List<DashboardShortcutDTO> shortcuts;
}
