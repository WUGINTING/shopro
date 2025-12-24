package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品分類
 * 支持 100 ~ 600 個分類
 */
@Entity
@Table(name = "product_category", indexes = {
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_sort_order", columnList = "sort_order")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 分類名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    // 父分類 ID（支持多層級分類）
    @Column(name = "parent_id")
    private Long parentId;

    // 分類描述
    @Column(columnDefinition = "NVARCHAR(500)")
    private String description;

    // 分類圖片
    @Column(length = 500)
    private String image;

    // 排序
    @Column(name = "sort_order")
    private Integer sortOrder;

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
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
