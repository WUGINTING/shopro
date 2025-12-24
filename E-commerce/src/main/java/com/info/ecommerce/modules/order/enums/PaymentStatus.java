package com.info.ecommerce.modules.order.enums;

/**
 * 付款狀態
 */
public enum PaymentStatus {
    PENDING("待付款"),
    PAID("已付款"),
    REFUNDING("退款中"),
    REFUNDED("已退款");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
