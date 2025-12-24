package com.info.ecommerce.modules.crm.entity;

import com.info.ecommerce.modules.crm.enums.PointType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 積點紀錄表
 */
@Entity
@Table(name = "crm_point_record", indexes = {
    @Index(name = "idx_point_member_id", columnList = "member_id"),
    @Index(name = "idx_point_type", columnList = "point_type"),
    @Index(name = "idx_point_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 會員 ID
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 積點類型
    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false, length = 20)
    private PointType pointType;

    // 積點數量（正數為增加，負數為減少）
    @Column(nullable = false)
    private Integer points;

    // 變更後餘額
    @Column(nullable = false)
    private Integer balanceAfter;

    // 關聯訂單 ID
    @Column(name = "order_id")
    private Long orderId;

    // 原因描述
    @Column(columnDefinition = "NVARCHAR(500)")
    private String reason;

    // 到期時間
    @Column
    private LocalDateTime expiresAt;

    // 創建時間
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
