package com.info.ecommerce.modules.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 自訂頁面表
 */
@Entity
@Table(name = "crm_custom_page", indexes = {
    @Index(name = "idx_page_slug", columnList = "slug"),
    @Index(name = "idx_page_enabled", columnList = "enabled")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 頁面標題
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String title;

    // 頁面別名（URL）
    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    // 頁面內容
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    // SEO 標題
    @Column(columnDefinition = "NVARCHAR(100)")
    private String metaTitle;

    // SEO 描述
    @Column(columnDefinition = "NVARCHAR(300)")
    private String metaDescription;

    // SEO 關鍵字
    @Column(columnDefinition = "NVARCHAR(200)")
    private String metaKeywords;

    // 是否啟用
    @Column(nullable = false)
    private Boolean enabled;

    // 排序
    @Column
    private Integer sortOrder;

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
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
