package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "訂單統計資料")
public class OrderStatisticsDTO {

    @Schema(description = "總訂單數")
    private Long totalOrders;

    @Schema(description = "總金額")
    private BigDecimal totalAmount;

    @Schema(description = "平均訂單金額")
    private BigDecimal averageOrderAmount;

    @Schema(description = "每日訂單趨勢 (日期 -> 訂單數)")
    private Map<String, Long> dailyOrderTrend;

    @Schema(description = "金額分布 (金額區間 -> 訂單數)")
    private Map<String, Long> amountDistribution;

    @Schema(description = "狀態占比 (狀態 -> 訂單數)")
    private Map<String, Long> statusDistribution;

    @Schema(description = "每日金額趨勢 (日期 -> 總金額)")
    private Map<String, BigDecimal> dailyAmountTrend;
}
