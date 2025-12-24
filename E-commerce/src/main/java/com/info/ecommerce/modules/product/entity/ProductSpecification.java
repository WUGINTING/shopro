package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品規格
 * 支持多規格多價格
 */
@Entity
@Table(name = "product_specification", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_sku", columnList = "sku")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 規格名稱（例如：顏色:紅色,尺寸:L）
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String specName;

    // 規格 SKU
    @Column(unique = true, length = 50)
    private String sku;

    // 規格價格
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    // 規格成本
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;

    // 庫存數量
    @Column
    private Integer stock;

    // 規格圖片
    @Column(length = 500)
    private String image;

    // 重量（克）
    @Column
    private Integer weight;

    // 是否啟用
    @Column
    private Boolean enabled;

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
        if (this.enabled == null) {
            this.enabled = true;
        }
        if (this.stock == null) {
            this.stock = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
