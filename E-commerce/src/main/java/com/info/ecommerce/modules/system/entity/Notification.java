package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系統通知實體
 * 用於管理系統內的通知消息
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_read", columnList = "is_read"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用戶 ID（null 表示系統通知，所有用戶可見）
    @Column(name = "user_id")
    private Long userId;

    // 通知類型
    @Column(nullable = false, length = 50)
    private String type; // ORDER, INVENTORY, SYSTEM, etc.

    // 通知標題
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String title;

    // 通知內容
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String content;

    // 相關實體 ID（如訂單 ID、商品 ID 等）
    @Column(name = "related_id")
    private Long relatedId;

    // 相關實體類型
    @Column(name = "related_type", length = 50)
    private String relatedType;

    // 是否已讀
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    // 讀取時間
    @Column(name = "read_at")
    private LocalDateTime readAt;

    // 優先級（HIGH, MEDIUM, LOW）
    @Column(length = 20)
    @Builder.Default
    private String priority = "MEDIUM";

    // 圖標
    @Column(length = 50)
    private String icon;

    // 顏色
    @Column(length = 20)
    private String color;

    // 創建時間
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
        if (priority == null) {
            priority = "MEDIUM";
        }
    }
}

