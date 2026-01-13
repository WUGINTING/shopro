package com.info.ecommerce.modules.calendar.dto;

import com.info.ecommerce.modules.calendar.enums.CalendarEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "行事曆事件資訊")
public class CalendarEventDTO {

    @Schema(description = "事件 ID", example = "1")
    private Long id;

    @NotNull(message = "事件類型不可為空")
    @Schema(description = "事件類型", example = "PRODUCT_LISTING")
    private CalendarEventType type;

    @NotNull(message = "關聯實體ID不可為空")
    @Schema(description = "關聯實體ID（商品ID、活動ID等）", example = "1")
    private Long entityId;

    @NotBlank(message = "關聯實體類型不可為空")
    @Size(max = 50, message = "關聯實體類型最多 50 字")
    @Schema(description = "關聯實體類型", example = "PRODUCT")
    private String entityType;

    @NotNull(message = "開始時間不可為空")
    @Schema(description = "開始時間", example = "2024-12-01T00:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "結束時間不可為空")
    @Schema(description = "結束時間", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;

    @NotBlank(message = "事件標題不可為空")
    @Size(max = 255, message = "事件標題最多 255 字")
    @Schema(description = "事件標題", example = "商品上架")
    private String title;

    @Size(max = 2000, message = "事件描述最多 2000 字")
    @Schema(description = "事件描述", example = "商品上架說明")
    private String description;

    @Size(max = 20, message = "顏色編碼最多 20 字")
    @Schema(description = "顏色編碼", example = "#3498db")
    private String color;

    @Schema(description = "創建者ID", example = "1")
    private Long createdBy;

    @Schema(description = "創建時間", example = "2024-11-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新者ID", example = "1")
    private Long updatedBy;

    @Schema(description = "更新時間", example = "2024-11-01T10:00:00")
    private LocalDateTime updatedAt;
}

