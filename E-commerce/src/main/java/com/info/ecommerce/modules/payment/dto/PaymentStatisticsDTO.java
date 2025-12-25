package com.info.ecommerce.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付統計 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatisticsDTO {
    
    /**
     * 今日成交金額
     */
    private BigDecimal todayAmount;
    
    /**
     * 今日交易數量
     */
    private Long todayCount;
    
    /**
     * 今日成功率
     */
    private Double todaySuccessRate;
    
    /**
     * 本月成交金額
     */
    private BigDecimal monthAmount;
    
    /**
     * 本月交易數量
     */
    private Long monthCount;
    
    /**
     * 退款統計
     */
    private RefundStatistics refundStatistics;
    
    /**
     * 各支付閘道佔比
     */
    private java.util.Map<String, GatewayShare> gatewayShares;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RefundStatistics {
        private Long todayRefundCount;
        private BigDecimal todayRefundAmount;
        private Long monthRefundCount;
        private BigDecimal monthRefundAmount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GatewayShare {
        private String gateway;
        private Long count;
        private BigDecimal amount;
        private Double percentage;
    }
}
