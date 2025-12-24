package com.info.ecommerce.modules.order.enums;

/**
 * 物流狀態
 */
public enum ShippingStatus {
    PENDING("待出貨"),
    SHIPPED("已出貨"),
    DELIVERED("已送達"),
    RETURNED("已退貨");

    private final String description;

    ShippingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
