package com.info.ecommerce.modules.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 相冊圖片 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumImageDTO {

    private Long id;

    @NotNull(message = "相冊 ID 不能為空")
    private Long albumId;

    @NotBlank(message = "圖片 URL 不能為空")
    @Size(max = 500, message = "圖片 URL 長度不能超過 500 個字符")
    private String imageUrl;

    @NotBlank(message = "檔案名稱不能為空")
    @Size(max = 255, message = "檔案名稱長度不能超過 255 個字符")
    private String fileName;

    @Size(max = 200, message = "圖片標題長度不能超過 200 個字符")
    private String title;

    @Size(max = 500, message = "圖片描述長度不能超過 500 個字符")
    private String description;

    private Long fileSize;

    private String fileType;

    private Integer sortOrder;

    private LocalDateTime uploadedAt;
}
