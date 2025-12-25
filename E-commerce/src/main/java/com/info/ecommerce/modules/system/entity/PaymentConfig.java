package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 金流設定
 * 金流商設定、付款方式管理、退款政策
 */
@Entity
@Table(name = "payment_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 金流商名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String providerName; // ECPAY, NEWEBPAY, LINE_PAY, etc.

    // 是否啟用
    @Column(nullable = false)
    private Boolean enabled;

    // API 設定
    @Column(columnDefinition = "NVARCHAR(500)")
    private String apiKey;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String apiSecret;

    @Column(length = 500)
    private String apiEndpoint;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String merchantId;

    // 付款方式
    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String paymentMethod; // CREDIT_CARD, ATM, CVS, LINE_PAY, etc.

    @Column(columnDefinition = "NVARCHAR(200)")
    private String paymentMethodName;

    // 手續費設定
    @Column(nullable = false)
    private Boolean chargeTransactionFee;

    @Column(precision = 5, scale = 2)
    private java.math.BigDecimal transactionFeeRate;

    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal transactionFeeFixed;

    // 退款政策
    @Column(nullable = false)
    private Boolean autoRefundEnabled;

    @Column
    private Integer autoRefundDays;

    @Column(nullable = false)
    private Boolean requireRefundApproval;

    // 排序
    @Column(name = "sort_order")
    private Integer sortOrder;

    // 測試模式
    @Column(nullable = false)
    private Boolean testMode;

    // 備註
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String notes;

    // 時間戳記
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String updatedBy;

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
