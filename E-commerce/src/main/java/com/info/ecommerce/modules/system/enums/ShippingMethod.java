package com.info.ecommerce.modules.system.enums;

/**
 * 物流方式枚舉
 */
public enum ShippingMethod {
    HOME_DELIVERY("宅配到府"),
    STORE_PICKUP("門市取貨"),
    CVS_PICKUP("超商取貨"),
    SELF_PICKUP("自取");

    private final String displayName;

    ShippingMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
