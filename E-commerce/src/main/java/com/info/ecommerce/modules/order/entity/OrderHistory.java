package com.info.ecommerce.modules.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 訂單歷史記錄（歷史紀錄管理）
 */
@Entity
@Table(name = "order_history", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 訂單 ID
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // 操作類型
    @Column(name = "action_type", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String actionType;

    // 操作描述
    @Column(name = "action_description", columnDefinition = "NVARCHAR(500)")
    private String actionDescription;

    // 操作前狀態
    @Column(name = "old_status", columnDefinition = "NVARCHAR(50)")
    private String oldStatus;

    // 操作後狀態
    @Column(name = "new_status", columnDefinition = "NVARCHAR(50)")
    private String newStatus;

    // 操作者 ID
    @Column(name = "operator_id")
    private Long operatorId;

    // 操作者名稱
    @Column(name = "operator_name", columnDefinition = "NVARCHAR(100)")
    private String operatorName;

    // 建立時間
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
