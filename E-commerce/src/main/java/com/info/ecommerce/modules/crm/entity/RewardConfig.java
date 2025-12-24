package com.info.ecommerce.modules.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 獎勵設定表（入會禮/生日禮）
 */
@Entity
@Table(name = "crm_reward_config", indexes = {
    @Index(name = "idx_reward_type", columnList = "reward_type"),
    @Index(name = "idx_reward_enabled", columnList = "enabled")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 獎勵類型（WELCOME: 入會禮, BIRTHDAY: 生日禮, REFERRAL: 推薦回饋）
    @Column(name = "reward_type", nullable = false, length = 20)
    private String rewardType;

    // 獎勵名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    // 獎勵描述
    @Column(columnDefinition = "NVARCHAR(500)")
    private String description;

    // 獎勵積點
    @Column
    private Integer rewardPoints;

    // 優惠券 ID
    @Column(name = "coupon_id")
    private Long couponId;

    // 是否啟用
    @Column(nullable = false)
    private Boolean enabled;

    // 有效天數（生日禮適用）
    @Column(name = "valid_days")
    private Integer validDays;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
