package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物流設定
 * 物流商設定、運費規則、配送區域管理
 */
@Entity
@Table(name = "shipping_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 物流商名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String providerName; // BLACK_CAT, SEVEN_ELEVEN, FAMILY_MART, etc.

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

    // 配送方式
    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String shippingMethod; // HOME_DELIVERY, STORE_PICKUP, CVS_PICKUP, etc.

    @Column(columnDefinition = "NVARCHAR(200)")
    private String shippingMethodName;

    // 運費規則
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseShippingFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal freeShippingThreshold;

    @Column(precision = 10, scale = 2)
    private BigDecimal remoteAreaSurcharge;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String remoteAreaList; // JSON or comma-separated

    // 重量/體積限制
    @Column(precision = 10, scale = 2)
    private BigDecimal maxWeight;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxVolume;

    // 配送時間
    @Column
    private Integer estimatedDeliveryDays;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String deliveryTimeSlots; // JSON or comma-separated

    // 可配送區域
    @Column(columnDefinition = "NVARCHAR(2000)")
    private String availableAreas; // JSON or comma-separated

    @Column(columnDefinition = "NVARCHAR(2000)")
    private String unavailableAreas; // JSON or comma-separated

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
