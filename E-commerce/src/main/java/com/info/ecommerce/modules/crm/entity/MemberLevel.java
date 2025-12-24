package com.info.ecommerce.modules.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 會員等級表
 */
@Entity
@Table(name = "crm_member_level", indexes = {
    @Index(name = "idx_level_name", columnList = "name"),
    @Index(name = "idx_level_order", columnList = "level_order")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 等級名稱
    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(50)")
    private String name;

    // 等級順序
    @Column(name = "level_order", nullable = false)
    private Integer levelOrder;

    // 所需最低消費金額
    @Column(precision = 10, scale = 2)
    private BigDecimal minSpendAmount;

    // 折扣率 (0.0 - 1.0)
    @Column(precision = 3, scale = 2)
    private BigDecimal discountRate;

    // 積點倍率
    @Column(precision = 3, scale = 2)
    private BigDecimal pointsMultiplier;

    // 等級描述
    @Column(columnDefinition = "NVARCHAR(500)")
    private String description;

    // 等級圖示 URL
    @Column(length = 500)
    private String iconUrl;

    // 是否啟用
    @Column(nullable = false)
    private Boolean enabled;

    // 創建時間
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
        if (this.discountRate == null) {
            this.discountRate = BigDecimal.ONE;
        }
        if (this.pointsMultiplier == null) {
            this.pointsMultiplier = BigDecimal.ONE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
