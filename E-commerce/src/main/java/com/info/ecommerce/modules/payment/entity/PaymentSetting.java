package com.info.ecommerce.modules.payment.entity;

import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付設定實體
 */
@Entity
@Table(name = "payment_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 支付閘道類型
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false, unique = true, length = 20)
    private PaymentGateway gateway;

    // 是否啟用
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    // 顯示名稱
    @Column(name = "display_name", columnDefinition = "NVARCHAR(100)")
    private String displayName;

    // 說明文字
    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    private String description;

    // 抽成比率 (%)
    @Column(name = "commission_rate", precision = 5, scale = 2)
    private java.math.BigDecimal commissionRate;

    // 維護狀態
    @Column(name = "maintenance_mode", nullable = false)
    private Boolean maintenanceMode;

    // 維護說明
    @Column(name = "maintenance_message", columnDefinition = "NVARCHAR(500)")
    private String maintenanceMessage;

    // 排序順序
    @Column(name = "sort_order")
    private Integer sortOrder;

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
        if (enabled == null) {
            enabled = true;
        }
        if (maintenanceMode == null) {
            maintenanceMode = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
