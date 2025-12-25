package com.info.ecommerce.modules.marketing.entity;

import com.info.ecommerce.modules.marketing.enums.CampaignStatus;
import com.info.ecommerce.modules.marketing.enums.CampaignType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 營銷活動資料表
 */
@Entity
@Table(name = "marketing_campaign", indexes = {
    @Index(name = "idx_campaign_type", columnList = "type"),
    @Index(name = "idx_campaign_status", columnList = "status"),
    @Index(name = "idx_campaign_start_date", columnList = "start_date"),
    @Index(name = "idx_campaign_end_date", columnList = "end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketingCampaign {

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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignType type;

    // 活動狀態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status;

    // 開始日期
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 結束日期
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 折扣率 (百分比)
    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;

    // 折扣金額
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    // 最低購買金額
    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    // 目標客群
    @Column(name = "target_audience", columnDefinition = "NVARCHAR(500)")
    private String targetAudience;

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
        if (this.status == null) {
            this.status = CampaignStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
