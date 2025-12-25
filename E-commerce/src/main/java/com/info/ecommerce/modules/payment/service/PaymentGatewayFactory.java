package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 支付閘道工廠服務
 */
@Service
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final LinePayService linePayService;
    private final EcPayService ecPayService;

    /**
     * 根據支付閘道類型取得對應的服務
     */
    public PaymentGatewayService getPaymentGatewayService(PaymentGateway gateway) {
        return switch (gateway) {
            case LINE_PAY -> linePayService;
            case ECPAY -> ecPayService;
            case MANUAL -> throw new UnsupportedOperationException("手動付款不需要支付閘道服務");
            default -> throw new IllegalArgumentException("不支援的支付閘道: " + gateway);
        };
    }
}
