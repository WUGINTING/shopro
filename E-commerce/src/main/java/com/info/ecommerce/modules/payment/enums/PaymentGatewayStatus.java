package com.info.ecommerce.modules.payment.enums;

/**
 * 支付閘道交易狀態
 */
public enum PaymentGatewayStatus {
    INITIATED("已發起"),
    PROCESSING("處理中"),
    SUCCESS("成功"),
    FAILED("失敗"),
    CANCELLED("已取消"),
    EXPIRED("已過期");

    private final String description;

    PaymentGatewayStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
