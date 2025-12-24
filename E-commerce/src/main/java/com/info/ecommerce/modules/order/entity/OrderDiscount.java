package com.info.ecommerce.modules.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 訂單折扣記錄（折扣管理）
 */
@Entity
@Table(name = "order_discounts", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單 ID
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // 折扣類型
    @Column(name = "discount_type", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String discountType;

    // 折扣代碼
    @Column(name = "discount_code", length = 50)
    private String discountCode;

    // 折扣金額
    @Column(name = "discount_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountAmount;

    // 折扣百分比
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    // 折扣描述
    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    private String description;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
