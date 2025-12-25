package com.info.ecommerce.modules.payment.dto;

import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付回應 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    
    /**
     * 支付閘道類型
     */
    private PaymentGateway gateway;
    
    /**
     * 支付狀態
     */
    private PaymentGatewayStatus status;
    
    /**
     * 支付閘道交易 ID
     */
    private String transactionId;
    
    /**
     * 訂單編號
     */
    private String orderNumber;
    
    /**
     * 支付金額
     */
    private BigDecimal amount;
    
    /**
     * 支付 URL (用於重導向到支付頁面)
     */
    private String paymentUrl;
    
    /**
     * Web 支付 URL
     */
    private String webPaymentUrl;
    
    /**
     * App 支付 URL
     */
    private String appPaymentUrl;
    
    /**
     * 錯誤訊息
     */
    private String errorMessage;
    
    /**
     * 支付閘道原始回應
     */
    private String rawResponse;
}
