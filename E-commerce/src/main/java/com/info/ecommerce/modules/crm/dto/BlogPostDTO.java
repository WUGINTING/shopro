package com.info.ecommerce.modules.crm.dto;

import com.info.ecommerce.modules.crm.enums.BlogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "部落格文章資訊")
public class BlogPostDTO {

    @Schema(description = "文章 ID", example = "1")
    private Long id;

    @NotBlank(message = "文章標題不可為空")
    @Size(max = 200, message = "文章標題最多 200 字")
    @Schema(description = "文章標題", example = "2024 春季新品發布")
    private String title;

    @NotBlank(message = "文章別名不可為空")
    @Size(max = 100, message = "文章別名最多 100 字")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "文章別名只能包含小寫字母、數字和連字號")
    @Schema(description = "文章別名（URL）", example = "2024-spring-collection")
    private String slug;

    @Schema(description = "文章內容")
    private String content;

    @Size(max = 500, message = "摘要最多 500 字")
    @Schema(description = "摘要")
    private String summary;

    @Size(max = 500, message = "封面圖片 URL 最多 500 字")
    @Schema(description = "封面圖片 URL")
    private String coverImageUrl;

    @Schema(description = "文章狀態", example = "DRAFT")
    private BlogStatus status;

    @Schema(description = "作者 ID", example = "1")
    private Long authorId;

    @Size(max = 100, message = "作者名稱最多 100 字")
    @Schema(description = "作者名稱", example = "張小華")
    private String authorName;

    @Size(max = 500, message = "標籤最多 500 字")
    @Schema(description = "標籤（逗號分隔）", example = "春季,新品,促銷")
    private String tags;

    @Size(max = 100, message = "SEO 標題最多 100 字")
    @Schema(description = "SEO 標題")
    private String metaTitle;

    @Size(max = 300, message = "SEO 描述最多 300 字")
    @Schema(description = "SEO 描述")
    private String metaDescription;

    @Size(max = 200, message = "SEO 關鍵字最多 200 字")
    @Schema(description = "SEO 關鍵字")
    private String metaKeywords;

    @Schema(description = "瀏覽次數", example = "150")
    private Integer viewCount;

    @Schema(description = "排程發布時間")
    private LocalDateTime scheduledAt;

    @Schema(description = "排程下架時間")
    private LocalDateTime scheduledUnpublishAt;

    @Schema(description = "發布時間")
    private LocalDateTime publishedAt;
}
