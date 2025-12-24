package com.info.ecommerce.modules.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 獎勵領取紀錄表
 */
@Entity
@Table(name = "crm_reward_claim", indexes = {
    @Index(name = "idx_reward_claim_member_id", columnList = "member_id"),
    @Index(name = "idx_reward_claim_config_id", columnList = "reward_config_id"),
    @Index(name = "idx_reward_claim_claimed_at", columnList = "claimed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 會員 ID
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 獎勵設定 ID
    @Column(name = "reward_config_id", nullable = false)
    private Long rewardConfigId;

    // 獎勵類型
    @Column(name = "reward_type", nullable = false, length = 20)
    private String rewardType;

    // 獲得積點
    @Column
    private Integer pointsReceived;

    // 優惠券 ID
    @Column(name = "coupon_id")
    private Long couponId;

    // 領取時間
    @Column(name = "claimed_at", nullable = false)
    private LocalDateTime claimedAt;

    @PrePersist
    public void prePersist() {
        this.claimedAt = LocalDateTime.now();
    }
}
