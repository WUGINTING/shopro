package com.info.ecommerce.modules.system.enums;

/**
 * 通知類型枚舉
 */
public enum NotificationType {
    EMAIL("電子郵件"),
    SMS("簡訊"),
    PUSH("推播通知"),
    IN_APP("站內通知");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
