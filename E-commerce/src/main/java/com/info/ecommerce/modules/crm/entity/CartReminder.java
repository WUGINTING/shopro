package com.info.ecommerce.modules.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 購物車未結帳提醒表
 */
@Entity
@Table(name = "crm_cart_reminder", indexes = {
    @Index(name = "idx_cart_reminder_member_id", columnList = "member_id"),
    @Index(name = "idx_cart_reminder_sent", columnList = "is_sent"),
    @Index(name = "idx_cart_reminder_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 會員 ID
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 購物車內容摘要
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String cartSummary;

    // 購物車商品數量
    @Column(nullable = false)
    private Integer itemCount;

    // 是否已發送提醒
    @Column(name = "is_sent", nullable = false)
    private Boolean isSent;

    // 提醒發送時間
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // 創建時間
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isSent == null) {
            this.isSent = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
