package com.info.ecommerce.modules.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "store")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 商店名稱：改為 NVARCHAR
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(length = 500)
    private String logo;

    @Column(length = 20)
    private String themeLevel;

    @Column(length = 10)
    private String defaultCurrency;

    @Column
    private Boolean invoiceEnabled;

    // SEO 設定：這些通常包含中文，皆改為 NVARCHAR
    @Column(columnDefinition = "NVARCHAR(100)")
    private String metaTitle;

    @Column(columnDefinition = "NVARCHAR(300)")
    private String metaDescription;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String metaKeywords;

    private LocalTime businessStart;

    private LocalTime businessEnd;

    // 地址：改為 NVARCHAR
    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
