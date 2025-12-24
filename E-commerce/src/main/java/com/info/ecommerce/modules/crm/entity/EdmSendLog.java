package com.info.ecommerce.modules.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EDM 發送紀錄表
 */
@Entity
@Table(name = "crm_edm_send_log", indexes = {
    @Index(name = "idx_edm_log_campaign_id", columnList = "campaign_id"),
    @Index(name = "idx_edm_log_member_id", columnList = "member_id"),
    @Index(name = "idx_edm_log_sent_at", columnList = "sent_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdmSendLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EDM 活動 ID
    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    // 會員 ID
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 收件人電子郵件
    @Column(nullable = false, length = 100)
    private String recipientEmail;

    // 是否成功發送
    @Column(nullable = false)
    private Boolean success;

    // 錯誤訊息
    @Column(columnDefinition = "NVARCHAR(500)")
    private String errorMessage;

    // 發送時間
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    // 開信時間
    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    // 點擊時間
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;

    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
    }
}
