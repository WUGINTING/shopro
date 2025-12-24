package com.info.ecommerce.modules.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系統附件表
 * 用於管理所有上傳的圖片和附件
 */
@Entity
@Table(name = "sys_attachment", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_created_by", columnList = "created_by"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 原始檔名
    @Column(name = "file_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String fileName;

    // 儲存路徑 / Key
    @Column(name = "file_path", nullable = false, columnDefinition = "NVARCHAR(500)")
    private String filePath;

    // 檔案大小（Bytes）
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    // MIME 類型
    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    // 用途分類
    @Column(name = "category", columnDefinition = "NVARCHAR(50)")
    private String category;

    // 縮圖路徑
    @Column(name = "thumbnail_path", columnDefinition = "NVARCHAR(500)")
    private String thumbnailPath;

    // 上傳者 ID
    @Column(name = "created_by", columnDefinition = "NVARCHAR(100)")
    private String createdBy;

    // 上傳時間
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
