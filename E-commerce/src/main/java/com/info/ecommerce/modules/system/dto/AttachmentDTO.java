package com.info.ecommerce.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "附件資訊")
public class AttachmentDTO {

    @Schema(description = "附件 ID", example = "1")
    private Long id;

    @Schema(description = "原始檔名", example = "cat.jpg")
    private String fileName;

    @Schema(description = "檔案路徑", example = "/uploads/2024/01/uuid.jpg")
    private String filePath;

    @Schema(description = "檔案大小（Bytes）", example = "102400")
    private Long fileSize;

    @Schema(description = "MIME 類型", example = "image/jpeg")
    private String fileType;

    @Schema(description = "用途分類", example = "UserAvatar")
    private String category;

    @Schema(description = "縮圖路徑", example = "/uploads/2024/01/uuid_thumb.jpg")
    private String thumbnailPath;

    @Schema(description = "上傳者 ID", example = "user123")
    private String createdBy;

    @Schema(description = "上傳時間", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;
}
