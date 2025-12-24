package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 票券商品
 */
@Entity
@Table(name = "product_voucher", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 票券有效期開始
    @Column
    private LocalDateTime validFrom;

    // 票券有效期結束
    @Column
    private LocalDateTime validTo;

    // 票券使用說明
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String usageInstructions;

    // 是否可退款
    @Column
    private Boolean refundable;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.refundable == null) {
            this.refundable = false;
        }
    }
}
