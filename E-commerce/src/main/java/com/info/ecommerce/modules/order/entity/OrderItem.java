package com.info.ecommerce.modules.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 訂單項目（支援每張訂單包含多個商品項目）
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_product_id", columnList = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單 ID
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 商品名稱
    @Column(name = "product_name", nullable = false, columnDefinition = "NVARCHAR(200)")
    private String productName;

    // 商品 SKU
    @Column(name = "product_sku", length = 50)
    private String productSku;

    // 商品規格
    @Column(name = "product_spec", columnDefinition = "NVARCHAR(200)")
    private String productSpec;

    // 單價
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    // 數量
    @Column(nullable = false)
    private Integer quantity;

    // 小計金額
    @Column(name = "subtotal_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotalAmount;

    // 折扣金額
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    // 實際金額
    @Column(name = "actual_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal actualAmount;
}
