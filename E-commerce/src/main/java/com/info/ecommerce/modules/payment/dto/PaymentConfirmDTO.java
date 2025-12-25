package com.info.ecommerce.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付確認 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfirmDTO {
    
    /**
     * 支付閘道交易 ID
     */
    private String transactionId;
    
    /**
     * 訂單編號
     */
    private String orderNumber;
    
    /**
     * 確認碼 (用於某些支付閘道)
     */
    private String confirmationCode;
}
