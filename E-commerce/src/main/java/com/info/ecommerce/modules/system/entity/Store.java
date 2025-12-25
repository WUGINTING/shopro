package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 門市管理
 * O2O 支援 - 門市 CRUD、營業時間、庫存綁定、取貨設定
 */
@Entity
@Table(name = "store", indexes = {
    @Index(name = "idx_store_code", columnList = "store_code", unique = true),
    @Index(name = "idx_enabled", columnList = "enabled")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 門市代碼
    @Column(nullable = false, unique = true, length = 50, name = "store_code")
    private String storeCode;

    // 門市名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String storeName;

    // 聯絡資訊
    @Column(columnDefinition = "NVARCHAR(50)")
    private String phoneNumber;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String email;

    // 地址資訊
    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String city;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String district;

    @Column(columnDefinition = "NVARCHAR(20)")
    private String postalCode;

    // 地理位置
    @Column(precision = 10, scale = 7)
    private java.math.BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private java.math.BigDecimal longitude;

    // 營業時間
    // Note: Using JSON format for flexibility. Example: {"monday": "09:00-18:00", "tuesday": "09:00-18:00"}
    @Column(columnDefinition = "NVARCHAR(200)")
    private String businessHours; // JSON format: {"monday": "09:00-18:00", ...}

    // Note: Using JSON format for flexibility. Example: ["sunday", "holiday"]
    @Column(columnDefinition = "NVARCHAR(200)")
    private String closedDays; // JSON array: ["sunday", "holiday"]

    // 門市狀態
    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Boolean allowPickup; // 允許門市取貨

    @Column(nullable = false)
    private Boolean allowPayment; // 允許門市付款

    // 庫存綁定
    @Column(name = "warehouse_id")
    private Long warehouseId;

    // 店長資訊
    @Column(name = "manager_id")
    private Long managerId;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String managerName;

    // 門市圖片
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String images; // JSON array of image URLs

    // 門市描述
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String description;

    // 取貨設定
    @Column
    private Integer pickupPreparationHours; // 取貨準備時間（小時）

    @Column(columnDefinition = "NVARCHAR(500)")
    private String pickupInstructions; // 取貨說明

    // 排序
    @Column(name = "sort_order")
    private Integer sortOrder;

    // 備註
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String notes;

    // 時間戳記
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
