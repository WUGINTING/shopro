package com.info.ecommerce.modules.system.enums;

/**
 * 操作類型枚舉
 */
public enum OperationType {
    LOGIN("登入"),
    LOGOUT("登出"),
    CREATE("新增"),
    UPDATE("更新"),
    DELETE("刪除"),
    QUERY("查詢"),
    EXPORT("匯出"),
    IMPORT("匯入"),
    APPROVE("審核"),
    REJECT("拒絕");

    private final String displayName;

    OperationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
