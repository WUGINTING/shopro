package com.info.ecommerce.modules.payment.dto;

import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付交易查詢 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionDTO {
    
    private Long id;
    private Long orderId;
    private String orderNumber;
    private PaymentGateway gateway;
    private String transactionId;
    private PaymentGatewayStatus status;
    private BigDecimal amount;
    private String currency;
    private String paymentUrl;
    private String errorMessage;
    private String rawResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 客戶資訊
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
