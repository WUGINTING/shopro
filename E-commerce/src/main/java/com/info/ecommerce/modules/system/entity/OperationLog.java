package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日誌
 * 記錄系統所有重要操作、登入紀錄、敏感操作追蹤
 */
@Entity
@Table(name = "operation_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_operation_type", columnList = "operation_type"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 操作用戶
    @Column(name = "user_id")
    private Long userId;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String username;

    // 操作類型
    @Column(nullable = false, columnDefinition = "NVARCHAR(50)", name = "operation_type")
    private String operationType; // LOGIN, LOGOUT, CREATE, UPDATE, DELETE, etc.

    // 操作模組
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String module; // PRODUCT, ORDER, CUSTOMER, SYSTEM, etc.

    // 操作描述
    @Column(nullable = false, columnDefinition = "NVARCHAR(500)")
    private String description;

    // 操作對象
    @Column(columnDefinition = "NVARCHAR(100)")
    private String targetType; // Entity type

    @Column(name = "target_id")
    private Long targetId;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String targetName;

    // 請求資訊
    @Column(length = 10)
    private String requestMethod; // GET, POST, PUT, DELETE

    @Column(length = 500)
    private String requestUrl;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String requestParams;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String requestBody;

    // 回應資訊
    @Column
    private Integer responseStatus;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String responseBody;

    // IP 與瀏覽器資訊
    @Column(length = 50)
    private String ipAddress;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String userAgent;

    // 執行時間（毫秒）
    @Column
    private Long executionTime;

    // 是否成功
    @Column(nullable = false)
    private Boolean success;

    // 錯誤訊息
    @Column(columnDefinition = "NVARCHAR(2000)")
    private String errorMessage;

    // 敏感操作標記
    @Column(nullable = false)
    private Boolean sensitive;

    // 備註
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String notes;

    // 時間戳記
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
