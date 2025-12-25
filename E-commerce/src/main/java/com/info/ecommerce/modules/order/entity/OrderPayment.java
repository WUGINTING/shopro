package com.info.ecommerce.modules.order.entity;

import com.info.ecommerce.modules.order.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 付款記錄（金流管理）
 */
@Entity
@Table(name = "order_payments", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_payment_status", columnList = "payment_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單 ID
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // 付款狀態
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    // 付款方式
    @Column(name = "payment_method", columnDefinition = "NVARCHAR(50)")
    private String paymentMethod;

    // 付款金額
    @Column(name = "payment_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal paymentAmount;

    // 付款交易號
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    // 支付閘道類型
    @Column(name = "payment_gateway", length = 20)
    private String paymentGateway;

    // 支付閘道交易 ID
    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;

    // 付款時間
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    // 退款金額
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    // 退款時間
    @Column(name = "refund_time")
    private LocalDateTime refundTime;

    // 備註
    @Column(columnDefinition = "NVARCHAR(500)")
    private String notes;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
