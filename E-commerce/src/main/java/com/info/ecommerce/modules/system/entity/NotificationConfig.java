package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知設定
 * Email、SMS 設定、通知模板管理、通知觸發規則
 */
@Entity
@Table(name = "notification_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 通知類型
    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String notificationType; // EMAIL, SMS, PUSH, etc.

    // 是否啟用
    @Column(nullable = false)
    private Boolean enabled;

    // Email 設定
    @Column(columnDefinition = "NVARCHAR(200)")
    private String smtpHost;

    @Column
    private Integer smtpPort;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String smtpUsername;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String smtpPassword;

    @Column(nullable = false)
    private Boolean smtpUseSsl;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String fromEmail;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String fromName;

    // SMS 設定
    @Column(columnDefinition = "NVARCHAR(100)")
    private String smsProvider; // TWILIO, AWS_SNS, etc.

    @Column(columnDefinition = "NVARCHAR(500)")
    private String smsApiKey;

    @Column(columnDefinition =="NVARCHAR(500)")
    private String smsApiSecret;

    @Column(columnDefinition = "NVARCHAR(50)")
    private String smsFromNumber;

    // 通知事件設定
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String eventType; // ORDER_CREATED, ORDER_SHIPPED, PASSWORD_RESET, etc.

    @Column(columnDefinition = "NVARCHAR(200)")
    private String eventName;

    // 通知模板
    @Column(columnDefinition = "NVARCHAR(500)")
    private String templateSubject;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String templateBody;

    // 觸發條件
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String triggerConditions; // JSON format

    // 發送設定
    @Column(nullable = false)
    private Boolean sendImmediately;

    @Column
    private Integer delayMinutes;

    @Column(nullable = false)
    private Boolean allowBatchSend;

    @Column
    private Integer batchSize;

    // 重試設定
    @Column(nullable = false)
    private Integer maxRetries;

    @Column
    private Integer retryIntervalMinutes;

    // 排序
    @Column(name = "sort_order")
    private Integer sortOrder;

    // 測試模式
    @Column(nullable = false)
    private Boolean testMode;

    // 備註
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String notes;

    // 時間戳記
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
