package com.info.ecommerce.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "商品分類")
public class ProductCategoryDTO {

    @Schema(description = "分類 ID", example = "1")
    private Long id;

    @NotBlank(message = "分類名稱不可為空")
    @Size(max = 100, message = "分類名稱最多 100 字")
    @Schema(description = "分類名稱", example = "服飾")
    private String name;

    @Schema(description = "父分類 ID", example = "0")
    private Long parentId;

    @Size(max = 500, message = "分類描述最多 500 字")
    @Schema(description = "分類描述", example = "各類服飾商品")
    private String description;

    @Schema(description = "分類圖片", example = "/images/category.jpg")
    private String image;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;
}
