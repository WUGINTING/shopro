package com.info.ecommerce.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "商品圖片")
public class ProductImageDTO {

    @Schema(description = "圖片 ID", example = "1")
    private Long id;

    @NotNull(message = "商品 ID 不可為空")
    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @NotBlank(message = "圖片 URL 不可為空")
    @Schema(description = "圖片 URL", example = "/images/product1.jpg")
    private String imageUrl;

    @Schema(description = "相冊圖片 ID（若此圖片來自相冊系統，此欄位記錄原始相冊圖片的 ID，用於追蹤圖片來源）", example = "1")
    private Long albumImageId;

    @Schema(description = "圖片類型", example = "MAIN")
    private String imageType;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否主圖", example = "true")
    private Boolean isPrimary;
}
