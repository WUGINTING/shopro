package com.info.ecommerce.modules.order.enums;

/**
 * 取貨方式 (O2O 支援)
 */
public enum PickupType {
    DELIVERY("宅配"),
    STORE_PICKUP("門市取貨"),
    CROSS_STORE_PICKUP("跨店取貨");

    private final String description;

    PickupType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
