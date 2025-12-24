package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 貨到通知
 */
@Entity
@Table(name = "stock_notification", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_user_email", columnList = "user_email"),
    @Index(name = "idx_notified", columnList = "notified")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商品 ID
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 規格 ID
    @Column(name = "specification_id")
    private Long specificationId;

    // 用戶 Email
    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    // 用戶電話
    @Column(length = 20)
    private String userPhone;

    // 是否已通知
    @Column
    private Boolean notified;

    // 通知時間
    @Column
    private LocalDateTime notifiedAt;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.notified == null) {
            this.notified = false;
        }
    }
}
