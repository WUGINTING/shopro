package com.info.ecommerce.modules.order.enums;

/**
 * 通知類型
 */
public enum NotificationType {
    EMAIL("電子郵件"),
    SMS("簡訊");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
