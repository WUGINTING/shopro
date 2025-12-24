package com.info.ecommerce.modules.product.entity;

import com.info.ecommerce.modules.product.enums.AlertLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品庫存
 * 支持庫存管理和警示
 */
@Entity
@Table(name = "product_inventory", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 規格 ID（可選）
    @Column(name = "specification_id")
    private Long specificationId;

    // 倉庫 ID
    @Column(name = "warehouse_id")
    private Long warehouseId;

    // 可用庫存
    @Column
    private Integer availableStock;

    // 鎖定庫存（已下單未出貨）
    @Column
    private Integer lockedStock;

    // 安全庫存量
    @Column
    private Integer safetyStock;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.availableStock == null) {
            this.availableStock = 0;
        }
        if (this.lockedStock == null) {
            this.lockedStock = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 檢查是否需要警示
     */
    public AlertLevel checkAlertLevel() {
        if (availableStock == 0) {
            return AlertLevel.OUT_OF_STOCK;
        }
        if (safetyStock != null) {
            // 使用雙精度運算避免整數除法精度損失
            double criticalThreshold = safetyStock * 0.5;
            if (availableStock <= criticalThreshold) {
                return AlertLevel.CRITICAL;
            }
            if (availableStock <= safetyStock) {
                return AlertLevel.LOW;
            }
        }
        return null;
    }
}
