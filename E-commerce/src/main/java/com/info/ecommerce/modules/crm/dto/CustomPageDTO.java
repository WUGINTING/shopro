package com.info.ecommerce.modules.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "自訂頁面資訊")
public class CustomPageDTO {

    @Schema(description = "頁面 ID", example = "1")
    private Long id;

    @NotBlank(message = "頁面標題不可為空")
    @Size(max = 200, message = "頁面標題最多 200 字")
    @Schema(description = "頁面標題", example = "關於我們")
    private String title;

    @NotBlank(message = "頁面別名不可為空")
    @Size(max = 100, message = "頁面別名最多 100 字")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "頁面別名只能包含小寫字母、數字和連字號")
    @Schema(description = "頁面別名（URL）", example = "about-us")
    private String slug;

    @Schema(description = "頁面內容")
    private String content;

    @Size(max = 100, message = "SEO 標題最多 100 字")
    @Schema(description = "SEO 標題")
    private String metaTitle;

    @Size(max = 300, message = "SEO 描述最多 300 字")
    @Schema(description = "SEO 描述")
    private String metaDescription;

    @Size(max = 200, message = "SEO 關鍵字最多 200 字")
    @Schema(description = "SEO 關鍵字")
    private String metaKeywords;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}
