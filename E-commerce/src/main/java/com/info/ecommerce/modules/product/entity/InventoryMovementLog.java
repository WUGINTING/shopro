package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movement_log", indexes = {
        @Index(name = "idx_inv_move_product_id", columnList = "product_id"),
        @Index(name = "idx_inv_move_spec_id", columnList = "specification_id"),
        @Index(name = "idx_inv_move_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovementLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "specification_id")
    private Long specificationId;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "change_type", nullable = false, length = 30)
    private String changeType;

    @Column(name = "source", nullable = false, length = 50)
    private String source;

    @Column(name = "change_quantity", nullable = false)
    private Integer changeQuantity;

    @Column(name = "before_stock", nullable = false)
    private Integer beforeStock;

    @Column(name = "after_stock", nullable = false)
    private Integer afterStock;

    @Column(name = "remark", columnDefinition = "NVARCHAR(500)")
    private String remark;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (changeQuantity == null) {
            changeQuantity = 0;
        }
        if (beforeStock == null) {
            beforeStock = 0;
        }
        if (afterStock == null) {
            afterStock = 0;
        }
    }
}
