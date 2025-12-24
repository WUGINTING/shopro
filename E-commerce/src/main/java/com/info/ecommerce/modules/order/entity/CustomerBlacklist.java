package com.info.ecommerce.modules.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 顧客黑名單（黑名單管理）
 */
@Entity
@Table(name = "customer_blacklist", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 客戶 ID
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // 客戶姓名
    @Column(name = "customer_name", columnDefinition = "NVARCHAR(100)")
    private String customerName;

    // 客戶電話
    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    // 客戶電子郵件
    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    // 黑名單原因
    @Column(name = "reason", nullable = false, columnDefinition = "NVARCHAR(500)")
    private String reason;

    // 是否啟用
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 建立者 ID
    @Column(name = "created_by")
    private Long createdBy;

    // 建立者名稱
    @Column(name = "created_by_name", columnDefinition = "NVARCHAR(100)")
    private String createdByName;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
