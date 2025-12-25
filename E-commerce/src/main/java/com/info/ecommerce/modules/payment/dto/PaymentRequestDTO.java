package com.info.ecommerce.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付請求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    
    /**
     * 訂單 ID
     */
    private Long orderId;
    
    /**
     * 訂單編號
     */
    private String orderNumber;
    
    /**
     * 支付金額
     */
    private BigDecimal amount;
    
    /**
     * 支付幣別
     */
    @Builder.Default
    private String currency = "TWD";
    
    /**
     * 商品描述
     */
    private String productName;
    
    /**
     * 付款人姓名
     */
    private String customerName;
    
    /**
     * 付款人電子郵件
     */
    private String customerEmail;
    
    /**
     * 付款人手機
     */
    private String customerPhone;
}
