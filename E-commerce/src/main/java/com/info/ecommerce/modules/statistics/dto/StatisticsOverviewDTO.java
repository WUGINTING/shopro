package com.info.ecommerce.modules.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 營運統計（指定期間）。銷售相關數字只計已付款 / 處理中 / 已完成的訂單。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsOverviewDTO {

    private String startDate;
    private String endDate;
    /** 銷售額 */
    private BigDecimal totalSales;
    /** 成交訂單數 */
    private long totalOrders;
    /** 下單顧客數（不重複） */
    private long totalCustomers;
    /** 新註冊 / 新建立的會員數 */
    private long newCustomers;
    /** 平均客單價 */
    private BigDecimal averageOrderValue;
    /** 期間內登記的退款金額 */
    private BigDecimal refundAmount;
    private List<ProductSales> topProducts;
    private List<CategorySales> topCategories;
    private List<DailySales> salesTrend;
    /** 訂單狀態（中文名稱）→ 筆數，含所有狀態 */
    private Map<String, Long> orderStatus;
    /** 付款方式 → 成交筆數 */
    private Map<String, Long> paymentMethods;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSales {
        private Long id;
        private String name;
        private long sales;
        private BigDecimal revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySales {
        private Long id;
        private String name;
        private long sales;
        private BigDecimal revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailySales {
        private String date;
        private BigDecimal sales;
        private long orders;
    }
}
