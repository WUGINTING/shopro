package com.info.ecommerce.modules.payment.enums;

/**
 * 支付閘道類型
 */
public enum PaymentGateway {
    LINE_PAY("LINE PAY", "台灣 LINE PAY 支付"),
    ECPAY("綠界科技", "ECPay 綠界支付"),
    MANUAL("手動付款", "手動記錄付款");

    private final String displayName;
    private final String description;

    PaymentGateway(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
