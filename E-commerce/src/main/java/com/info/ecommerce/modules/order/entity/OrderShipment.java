package com.info.ecommerce.modules.order.entity;

import com.info.ecommerce.modules.order.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 物流記錄（物流管理）
 */
@Entity
@Table(name = "order_shipments", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_tracking_number", columnList = "tracking_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單 ID
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // 物流狀態
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status", nullable = false, length = 20)
    private ShippingStatus shippingStatus;

    // 物流公司
    @Column(name = "shipping_company", columnDefinition = "NVARCHAR(100)")
    private String shippingCompany;

    // 物流單號
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    // 出貨時間
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    // 送達時間
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    // 收件人姓名
    @Column(name = "recipient_name", columnDefinition = "NVARCHAR(100)")
    private String recipientName;

    // 收件人電話
    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    // 收件地址
    @Column(name = "recipient_address", columnDefinition = "NVARCHAR(500)")
    private String recipientAddress;

    // 備註
    @Column(columnDefinition = "NVARCHAR(500)")
    private String notes;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
