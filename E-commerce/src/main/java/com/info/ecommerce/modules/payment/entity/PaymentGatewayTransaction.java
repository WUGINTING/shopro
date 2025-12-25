package com.info.ecommerce.modules.payment.entity;

import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付閘道交易記錄
 */
@Entity
@Table(name = "payment_gateway_transactions", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_gateway_status", columnList = "gateway,status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGatewayTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單 ID
    @Column(name = "order_id")
    private Long orderId;

    // 訂單編號
    @Column(name = "order_number", nullable = false, length = 100)
    private String orderNumber;

    // 支付閘道類型
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false, length = 20)
    private PaymentGateway gateway;

    // 支付閘道交易 ID
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    // 支付狀態
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentGatewayStatus status;

    // 支付金額
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    // 幣別
    @Column(name = "currency", length = 10)
    private String currency;

    // 支付 URL
    @Column(name = "payment_url", length = 500)
    private String paymentUrl;

    // 錯誤訊息
    @Column(name = "error_message", columnDefinition = "NVARCHAR(500)")
    private String errorMessage;

    // 支付閘道原始回應
    @Column(name = "raw_response", columnDefinition = "NVARCHAR(MAX)")
    private String rawResponse;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
