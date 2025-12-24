package com.info.ecommerce.modules.order.entity;

import com.info.ecommerce.modules.order.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 訂單通知記錄
 */
@Entity
@Table(name = "order_notifications", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_sent_at", columnList = "sent_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單 ID
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // 通知類型
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 20)
    private NotificationType notificationType;

    // 接收者
    @Column(name = "recipient", nullable = false, columnDefinition = "NVARCHAR(200)")
    private String recipient;

    // 通知主旨
    @Column(name = "subject", columnDefinition = "NVARCHAR(200)")
    private String subject;

    // 通知內容
    @Column(name = "content", columnDefinition = "NVARCHAR(MAX)")
    private String content;

    // 是否已發送
    @Column(name = "is_sent", nullable = false)
    private Boolean isSent;

    // 發送時間
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // 錯誤訊息
    @Column(name = "error_message", columnDefinition = "NVARCHAR(500)")
    private String errorMessage;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isSent == null) {
            isSent = false;
        }
    }
}
