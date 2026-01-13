package com.info.ecommerce.modules.calendar.dto;

import com.info.ecommerce.modules.calendar.enums.CalendarEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "行事曆事件查詢條件")
public class CalendarEventQueryDTO {

    @Schema(description = "事件類型", example = "PRODUCT_LISTING")
    private CalendarEventType type;

    @Schema(description = "關聯實體類型", example = "PRODUCT")
    private String entityType;

    @Schema(description = "關聯實體ID", example = "1")
    private Long entityId;

    @Schema(description = "開始時間（查詢起始點）", example = "2024-12-01T00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "結束時間（查詢結束點）", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "關鍵字搜尋（標題或描述）", example = "商品")
    private String keyword;
}

