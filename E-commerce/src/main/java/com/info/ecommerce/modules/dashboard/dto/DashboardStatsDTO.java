package com.info.ecommerce.modules.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "儀表板統計摘要")
public class DashboardStatsDTO {

    @Schema(description = "總商品數")
    private Long totalProducts;

    @Schema(description = "商品數變化百分比")
    private Double totalProductsChange;

    @Schema(description = "待處理訂單數")
    private Long pendingOrders;

    @Schema(description = "待處理訂單變化百分比")
    private Double pendingOrdersChange;

    @Schema(description = "總客戶數")
    private Long totalCustomers;

    @Schema(description = "客戶數變化百分比")
    private Double totalCustomersChange;

    @Schema(description = "本月銷售額")
    private BigDecimal monthlySales;

    @Schema(description = "銷售額變化百分比")
    private Double monthlySalesChange;
}
