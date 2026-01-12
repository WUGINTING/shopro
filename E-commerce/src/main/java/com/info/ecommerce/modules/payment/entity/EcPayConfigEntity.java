package com.info.ecommerce.modules.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ECPay 支付配置實體
 * 存儲 ECPay 的配置資訊
 */
@Entity
@Table(name = "ecpay_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcPayConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ECPay 商店代號 (MerchantID)
     */
    @Column(name = "merchant_id", nullable = false, length = 50)
    private String merchantId;

    /**
     * ECPay HashKey
     */
    @Column(name = "hash_key", nullable = false, length = 100)
    private String hashKey;

    /**
     * ECPay HashIV
     */
    @Column(name = "hash_iv", nullable = false, length = 100)
    private String hashIV;

    /**
     * ECPay API Base URL
     */
    @Column(name = "api_url", nullable = false, length = 200)
    private String apiUrl;

    /**
     * 付款完成返回 URL
     */
    @Column(name = "return_url", length = 500)
    private String returnUrl;

    /**
     * 付款結果通知 URL (OrderResultURL)
     */
    @Column(name = "notify_url", nullable = false, length = 500)
    private String notifyUrl;

    /**
     * 是否為測試環境
     */
    @Column(name = "sandbox", nullable = false)
    private Boolean sandbox;

    /**
     * 是否啟用
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    /**
     * 備註說明
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 建立時間
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    @Column(name = "updated_at", nullable = false)
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

