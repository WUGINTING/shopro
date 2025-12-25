package com.info.ecommerce.modules.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 相冊 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumDTO {

    private Long id;

    @NotBlank(message = "相冊名稱不能為空")
    @Size(max = 200, message = "相冊名稱長度不能超過 200 個字符")
    private String name;

    @Size(max = 1000, message = "相冊描述長度不能超過 1000 個字符")
    private String description;

    private String coverImageUrl;

    @Builder.Default
    private List<AlbumImageDTO> images = new ArrayList<>();

    private Integer imageCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
