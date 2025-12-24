package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品-標籤關聯表
 */
@Entity
@Table(name = "product_tag_relation", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_tag_id", columnList = "tag_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTagRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 標籤 ID
    @Column(name = "tag_id", nullable = false)
    private Long tagId;
}
