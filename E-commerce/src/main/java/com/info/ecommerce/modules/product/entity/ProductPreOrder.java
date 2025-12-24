package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 預購商品
 */
@Entity
@Table(name = "product_pre_order", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPreOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 預購開始時間
    @Column(nullable = false)
    private LocalDateTime preOrderStartTime;

    // 預購結束時間
    @Column(nullable = false)
    private LocalDateTime preOrderEndTime;

    // 預計出貨時間
    @Column
    private LocalDateTime estimatedShipTime;

    // 預購數量限制
    @Column
    private Integer quantityLimit;

    // 已預購數量
    @Column
    private Integer orderedQuantity;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.orderedQuantity == null) {
            this.orderedQuantity = 0;
        }
    }
}
