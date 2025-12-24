package com.info.ecommerce.modules.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 倉庫管理
 */
@Entity
@Table(name = "warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 倉庫名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    // 倉庫編號
    @Column(unique = true, length = 50)
    private String code;

    // 倉庫地址
    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    // 聯絡人
    @Column(columnDefinition = "NVARCHAR(50)")
    private String contactPerson;

    // 聯絡電話
    @Column(length = 20)
    private String contactPhone;

    // 是否啟用
    @Column
    private Boolean enabled;

    // 是否為預設倉庫
    @Column
    private Boolean isDefault;

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
        if (this.isDefault == null) {
            this.isDefault = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
