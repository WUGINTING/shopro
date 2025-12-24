package com.info.ecommerce.modules.product.enums;

/**
 * 庫存警示等級
 */
public enum AlertLevel {
    LOW("低庫存"),
    CRITICAL("嚴重缺貨"),
    OUT_OF_STOCK("無庫存");

    private final String description;

    AlertLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
