package com.info.ecommerce.modules.crm.entity;

import com.info.ecommerce.modules.crm.enums.BlogStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部落格文章表
 */
@Entity
@Table(name = "crm_blog_post", indexes = {
    @Index(name = "idx_blog_status", columnList = "status"),
    @Index(name = "idx_blog_slug", columnList = "slug"),
    @Index(name = "idx_blog_published_at", columnList = "published_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 文章標題
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String title;

    // 文章別名（URL）
    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    // 文章內容
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    // 摘要
    @Column(columnDefinition = "NVARCHAR(500)")
    private String summary;

    // 封面圖片 URL
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    // 文章狀態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BlogStatus status;

    // 作者 ID
    @Column(name = "author_id")
    private Long authorId;

    // 作者名稱
    @Column(name = "author_name", columnDefinition = "NVARCHAR(100)")
    private String authorName;

    // 標籤（逗號分隔）
    @Column(columnDefinition = "NVARCHAR(500)")
    private String tags;

    // SEO 標題
    @Column(name = "meta_title", columnDefinition = "NVARCHAR(100)")
    private String metaTitle;

    // SEO 描述
    @Column(name = "meta_description", columnDefinition = "NVARCHAR(300)")
    private String metaDescription;

    // SEO 關鍵字
    @Column(name = "meta_keywords", columnDefinition = "NVARCHAR(200)")
    private String metaKeywords;

    // 瀏覽次數
    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    // 排程發布時間
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    // 發布時間
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

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
        if (this.status == null) {
            this.status = BlogStatus.DRAFT;
        }
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
