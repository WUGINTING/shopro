package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品圖片
 * 每個商品支持 10 ~ 15 張圖片
 */
@Entity
@Table(name = "product_image", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_sort_order", columnList = "sort_order")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 圖片 URL
    @Column(nullable = false, length = 500)
    private String imageUrl;

    // 圖片類型（主圖、詳情圖等）
    @Column(length = 20)
    private String imageType;

    // 排序
    @Column(name = "sort_order")
    private Integer sortOrder;

    // 是否主圖
    @Column
    private Boolean isPrimary;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isPrimary == null) {
            this.isPrimary = false;
        }
    }
}
