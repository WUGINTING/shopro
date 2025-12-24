package com.info.ecommerce.modules.order.enums;

/**
 * 訂單狀態
 */
public enum OrderStatus {
    PENDING_PAYMENT("待付款"),
    PROCESSING("處理中"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    REFUNDED("已退款");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
