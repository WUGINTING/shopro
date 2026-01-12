package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系統設置實體
 * 統一管理系統設置、郵件設置、通知設置和安全設置
 */
@Entity
@Table(name = "settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 設置類型：SYSTEM, EMAIL, NOTIFICATION, SECURITY
    @Column(nullable = false, length = 50)
    private String settingType;

    // 設置鍵
    @Column(nullable = false, length = 100)
    private String settingKey;

    // 設置值（JSON 格式存儲）
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String settingValue;

    // 描述
    @Column(columnDefinition = "NVARCHAR(500)")
    private String description;

    // 時間戳記
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

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

