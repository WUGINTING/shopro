package com.info.ecommerce.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 促銷活動資料表
 */
@Entity
@Table(name = "promotion", indexes = {
    @Index(name = "idx_promotion_type", columnList = "type"),
    @Index(name = "idx_promotion_enabled", columnList = "enabled"),
    @Index(name = "idx_promotion_start_date", columnList = "start_date"),
    @Index(name = "idx_promotion_end_date", columnList = "end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 活動名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String name;

    // 活動描述
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String description;

    // 活動類型
    @Column(nullable = false, length = 50)
    private String type; // DISCOUNT, FULL_SHOP, FREE_SHIPPING, BUY_GIFT

    // 折扣類型
    @Column(name = "discount_type", length = 20)
    private String discountType; // PERCENTAGE, FIXED

    // 折扣值
    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;

    // 最低購買金額
    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    // 最大折扣金額
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    // 開始日期
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 結束日期
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 是否啟用
    @Column(nullable = false)
    private Boolean enabled;

    // 優先級
    @Column(nullable = false)
    private Integer priority;

    // 條件描述
    @Column(columnDefinition = "NVARCHAR(500)")
    private String conditions;

    // 創建時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
        if (this.priority == null) {
            this.priority = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

