package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系統基本設定
 * 網站基本資訊、SEO、營業時間、幣別稅率等設定
 */
@Entity
@Table(name = "system_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 網站基本資訊
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String siteName;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 500)
    private String faviconUrl;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String contactEmail;

    @Column(columnDefinition = "NVARCHAR(50)")
    private String contactPhone;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    // SEO 設定
    @Column(columnDefinition = "NVARCHAR(200)")
    private String metaTitle;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String metaDescription;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String metaKeywords;

    // 營業時間設定
    @Column(columnDefinition = "NVARCHAR(100)")
    private String businessHours;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String closedDays;

    // 幣別與稅率
    @Column(nullable = false, length = 10)
    private String defaultCurrency;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;

    // 訂單設定
    @Column(columnDefinition = "NVARCHAR(100)")
    private String orderNumberPrefix;

    @Column(nullable = false)
    private Integer autoOrderCancelMinutes;

    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String stockDeductionTiming; // ORDER_CREATED, PAYMENT_COMPLETED, SHIPPED

    // 會員設定
    @Column(nullable = false)
    private Boolean requireMemberApproval;

    @Column(nullable = false)
    private Integer pointsExpirationDays;

    @Column(nullable = false)
    private Integer minPasswordLength;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String passwordRegexPattern;

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
