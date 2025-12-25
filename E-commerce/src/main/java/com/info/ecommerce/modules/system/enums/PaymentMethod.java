package com.info.ecommerce.modules.system.enums;

/**
 * 付款方式枚舉
 */
public enum PaymentMethod {
    CREDIT_CARD("信用卡"),
    ATM("ATM 轉帳"),
    CVS("超商代碼繳費"),
    LINE_PAY("LINE Pay"),
    APPLE_PAY("Apple Pay"),
    GOOGLE_PAY("Google Pay"),
    COD("貨到付款"),
    STORE_PAYMENT("門市付款");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
