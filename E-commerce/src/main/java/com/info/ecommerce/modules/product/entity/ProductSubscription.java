package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 訂閱商品
 */
@Entity
@Table(name = "product_subscription", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 訂閱週期（天數）
    @Column
    private Integer subscriptionPeriodDays;

    // 訂閱週期類型（每日、每週、每月）
    @Column(length = 20)
    private String periodType;

    // 最少訂閱期數
    @Column
    private Integer minPeriods;

    // 最多訂閱期數
    @Column
    private Integer maxPeriods;

    // 是否自動續訂
    @Column
    private Boolean autoRenew;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.autoRenew == null) {
            this.autoRenew = true;
        }
    }
}
