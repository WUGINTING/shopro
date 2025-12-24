package com.info.ecommerce.modules.order.entity;

import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PickupType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 訂單主表
 * 支援訂單管理、O2O、暫存功能
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_order_number", columnList = "order_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單編號（唯一）
    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    // 客戶 ID
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // 客戶姓名
    @Column(name = "customer_name", columnDefinition = "NVARCHAR(100)")
    private String customerName;

    // 客戶電話
    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    // 客戶電子郵件
    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    // 訂單狀態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    // 取貨方式（O2O 支援）
    @Enumerated(EnumType.STRING)
    @Column(name = "pickup_type", nullable = false, length = 20)
    private PickupType pickupType;

    // 門市 ID（用於門市取貨）
    @Column(name = "store_id")
    private Long storeId;

    // 訂單金額小計
    @Column(name = "subtotal_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotalAmount;

    // 折扣金額
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    // 運費
    @Column(name = "shipping_fee", precision = 10, scale = 2)
    private BigDecimal shippingFee;

    // 訂單總金額
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    // 備註
    @Column(columnDefinition = "NVARCHAR(500)")
    private String notes;

    // 收貨地址
    @Column(name = "shipping_address", columnDefinition = "NVARCHAR(500)")
    private String shippingAddress;

    // 是否暫存訂單
    @Column(name = "is_draft", nullable = false)
    private Boolean isDraft;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 完成時間
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDraft == null) {
            isDraft = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
