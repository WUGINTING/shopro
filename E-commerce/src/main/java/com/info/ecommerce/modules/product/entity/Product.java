package com.info.ecommerce.modules.product.entity;

import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品主表
 * 支持 500 ~ 10,000 項商品
 */
@Entity
@Table(name = "product", indexes = {
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_sales_mode", columnList = "sales_mode")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String name;

    // 商品編號
    @Column(unique = true, length = 50)
    private String sku;

    // 商品描述
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    // 商品描述
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    // 商品分類
    @Column(name = "category_id")
    private Long categoryId;

    // 商品狀態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    // 銷售模式
    @Enumerated(EnumType.STRING)
    @Column(name = "sales_mode", nullable = false, length = 20)
    private ProductSalesMode salesMode;

    // 基礎價格
    @Column(precision = 10, scale = 2)
    private BigDecimal basePrice;

    // 銷售價格
    @Column(precision = 10, scale = 2)
    private BigDecimal salePrice;

    // 成本價格
    @Column(precision = 10, scale = 2)
    private BigDecimal costPrice;

    // 商品重量（克）
    @Column
    private Integer weight;

    // 購買數量限制（最小）
    @Column
    private Integer minPurchaseQuantity;

    // 購買數量限制（最大）
    @Column
    private Integer maxPurchaseQuantity;

    // 排序
    @Column
    private Integer sortOrder;

    // SEO 標題
    @Column(columnDefinition = "NVARCHAR(100)")
    private String metaTitle;

    // SEO 描述
    @Column(columnDefinition = "NVARCHAR(300)")
    private String metaDescription;

    // SEO 關鍵字
    @Column(columnDefinition = "NVARCHAR(200)")
    private String metaKeywords;

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
        if (this.status == null) {
            this.status = ProductStatus.DRAFT;
        }
        if (this.salesMode == null) {
            this.salesMode = ProductSalesMode.NORMAL;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
