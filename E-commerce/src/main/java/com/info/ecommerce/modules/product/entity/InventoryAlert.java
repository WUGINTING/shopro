package com.info.ecommerce.modules.product.entity;

import com.info.ecommerce.modules.product.enums.AlertLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 庫存警示記錄
 */
@Entity
@Table(name = "inventory_alert", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_alert_level", columnList = "alert_level"),
    @Index(name = "idx_resolved", columnList = "resolved")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 規格 ID
    @Column(name = "specification_id")
    private Long specificationId;

    // 警示等級
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_level", nullable = false, length = 20)
    private AlertLevel alertLevel;

    // 當前庫存
    @Column
    private Integer currentStock;

    // 安全庫存
    @Column
    private Integer safetyStock;

    // 警示訊息
    @Column(columnDefinition = "NVARCHAR(500)")
    private String message;

    // 是否已解決
    @Column
    private Boolean resolved;

    // 解決時間
    @Column
    private LocalDateTime resolvedAt;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.resolved == null) {
            this.resolved = false;
        }
    }
}
