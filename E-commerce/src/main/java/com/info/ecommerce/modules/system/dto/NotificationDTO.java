package com.info.ecommerce.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "通知")
public class NotificationDTO {

    @Schema(description = "通知 ID")
    private Long id;

    @Schema(description = "用戶 ID")
    private Long userId;

    @Schema(description = "通知類型", example = "ORDER")
    private String type;

    @Schema(description = "通知標題", example = "新訂單")
    private String title;

    @Schema(description = "通知內容", example = "您有一個新訂單需要處理")
    private String content;

    @Schema(description = "相關實體 ID")
    private Long relatedId;

    @Schema(description = "相關實體類型", example = "Order")
    private String relatedType;

    @Schema(description = "是否已讀", example = "false")
    private Boolean isRead;

    @Schema(description = "讀取時間")
    private LocalDateTime readAt;

    @Schema(description = "優先級", example = "HIGH")
    private String priority;

    @Schema(description = "圖標", example = "shopping_bag")
    private String icon;

    @Schema(description = "顏色", example = "primary")
    private String color;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;
}

