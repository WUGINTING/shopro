package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品標籤
 * 支持 100 個 ~ 不限
 */
@Entity
@Table(name = "product_tag", indexes = {
    @Index(name = "idx_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 標籤名稱
    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(50)")
    private String name;

    // 標籤顏色
    @Column(length = 20)
    private String color;

    // 標籤描述
    @Column(columnDefinition = "NVARCHAR(200)")
    private String description;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
