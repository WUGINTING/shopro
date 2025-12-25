package com.info.ecommerce.modules.album.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 相冊實體
 */
@Entity
@Table(name = "album", indexes = {
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 相冊名稱
    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String name;

    // 相冊描述
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String description;

    // 封面圖片 URL
    @Column(columnDefinition = "NVARCHAR(500)")
    private String coverImageUrl;

    // 相冊中的圖片
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AlbumImage> images = new ArrayList<>();

    // 創建時間
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at", nullable = false)
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

    // Helper method to add image
    public void addImage(AlbumImage image) {
        images.add(image);
        image.setAlbum(this);
    }

    // Helper method to remove image
    public void removeImage(AlbumImage image) {
        images.remove(image);
        image.setAlbum(null);
    }
}
