package com.info.ecommerce.modules.album.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 相冊圖片實體
 */
@Entity
@Table(name = "album_image", indexes = {
    @Index(name = "idx_album_id", columnList = "album_id"),
    @Index(name = "idx_uploaded_at", columnList = "uploaded_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 所屬相冊
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    // 圖片 URL
    @Column(nullable = false, columnDefinition = "NVARCHAR(500)")
    private String imageUrl;

    // 圖片檔案名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String fileName;

    // 圖片標題
    @Column(columnDefinition = "NVARCHAR(200)")
    private String title;

    // 圖片描述
    @Column(columnDefinition = "NVARCHAR(500)")
    private String description;

    // 檔案大小（bytes）
    @Column
    private Long fileSize;

    // 檔案類型（MIME type）
    @Column(length = 100)
    private String fileType;

    // 排序順序
    @Column
    private Integer sortOrder;

    // 上傳時間
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}
