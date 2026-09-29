package com.info.ecommerce.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 優惠券（折扣碼）
 * 顧客結帳時輸入代碼，由結帳金額計算套用；使用次數在下單時扣除、訂單取消時歸還。
 */
@Entity
@Table(name = "coupon", indexes = {
    @Index(name = "idx_coupon_code", columnList = "code", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 代碼（不分大小寫，儲存為大寫） */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String name;

    /** PERCENTAGE（折扣百分比）/ FIXED（折抵金額）/ FREE_SHIPPING（免運） */
    @Column(nullable = false, length = 20)
    private String type;

    /** PERCENTAGE 時為折扣百分比（10 = 打 9 折），FIXED 時為折抵金額 */
    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    /** 百分比折扣的折抵上限 */
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    /** 可使用總次數 */
    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Column(name = "used_count", nullable = false)
    private Integer usedCount;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(nullable = false)
    private Boolean enabled;

    /** 是否公開顯示在前台優惠頁（未公開的代碼只有拿到的人能用） */
    @Column(name = "public_visible")
    private Boolean publicVisible;

    /** 適用說明（顯示用） */
    @Column(columnDefinition = "NVARCHAR(500)")
    private String applicable;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
        if (this.usedCount == null) {
            this.usedCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
