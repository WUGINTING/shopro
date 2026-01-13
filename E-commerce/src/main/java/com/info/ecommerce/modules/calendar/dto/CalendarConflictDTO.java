package com.info.ecommerce.modules.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "時間衝突資訊")
public class CalendarConflictDTO {

    @Schema(description = "衝突類型", example = "PRODUCT_DELISTING_DURING_ACTIVITY")
    private String conflictType;

    @Schema(description = "衝突描述", example = "商品將在活動期間下架")
    private String description;

    @Schema(description = "衝突的事件ID列表")
    private List<Long> conflictingEventIds;

    @Schema(description = "衝突的事件標題列表")
    private List<String> conflictingEventTitles;

    @Schema(description = "建議修復方案", example = "延長商品下架時間或提前結束活動")
    private String suggestion;
}

