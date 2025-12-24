package com.info.ecommerce.modules.store.dto;

import com.info.ecommerce.modules.store.enums.BlockType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "首頁區塊")
public class HomepageBlockDTO {

    @Schema(description = "區塊 ID", example = "1")
    private Long id;

    @NotNull(message = "區塊類型不可為空")
    @Schema(description = "區塊類型", example = "BANNER")
    private BlockType blockType;

    @Schema(description = "區塊標題", example = "熱門商品")
    private String title;

    @NotNull(message = "排序不可為空")
    @Schema(description = "排序順序", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "區塊內容 (JSON 格式)", example = "{\"images\": [\"img1.jpg\", \"img2.jpg\"]}")
    private String content;
}
