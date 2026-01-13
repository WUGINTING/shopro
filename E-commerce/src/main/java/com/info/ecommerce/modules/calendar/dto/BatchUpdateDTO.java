package com.info.ecommerce.modules.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "批量更新請求")
public class BatchUpdateDTO {

    @NotEmpty(message = "事件ID列表不可為空")
    @Schema(description = "要更新的事件ID列表", example = "[1, 2, 3]")
    private List<Long> eventIds;

    @NotNull(message = "新的開始時間不可為空")
    @Schema(description = "新的開始時間", example = "2024-12-01T00:00:00")
    private LocalDateTime newStartTime;

    @NotNull(message = "新的結束時間不可為空")
    @Schema(description = "新的結束時間", example = "2024-12-31T23:59:59")
    private LocalDateTime newEndTime;

    @Schema(description = "是否同時更新關聯商品", example = "true")
    private Boolean updateRelatedProducts = false;

    @Schema(description = "是否同時更新關聯活動", example = "false")
    private Boolean updateRelatedActivities = false;
}

