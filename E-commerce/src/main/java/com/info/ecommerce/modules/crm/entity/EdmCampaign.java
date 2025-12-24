package com.info.ecommerce.modules.crm.entity;

import com.info.ecommerce.modules.crm.enums.EdmStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EDM 電子報活動表
 */
@Entity
@Table(name = "crm_edm_campaign", indexes = {
    @Index(name = "idx_edm_status", columnList = "status"),
    @Index(name = "idx_edm_scheduled_at", columnList = "scheduled_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdmCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 活動名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String name;

    // 郵件主旨
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String subject;

    // 郵件內容
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    // EDM 狀態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EdmStatus status;

    // 目標群組 ID（可選，null 表示全部會員）
    @Column(name = "target_group_id")
    private Long targetGroupId;

    // 排程發送時間
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    // 實際發送時間
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // 總發送數
    @Column(name = "total_sent")
    private Integer totalSent;

    // 成功數
    @Column(name = "success_count")
    private Integer successCount;

    // 失敗數
    @Column(name = "failure_count")
    private Integer failureCount;

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
            this.status = EdmStatus.DRAFT;
        }
        if (this.totalSent == null) {
            this.totalSent = 0;
        }
        if (this.successCount == null) {
            this.successCount = 0;
        }
        if (this.failureCount == null) {
            this.failureCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
