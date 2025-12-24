package com.info.ecommerce.modules.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批次發放積點資訊")
public class PointBatchDTO {

    @NotNull(message = "會員 ID 列表不可為空")
    @Size(min = 1, message = "至少需要一個會員 ID")
    @Schema(description = "會員 ID 列表")
    private List<Long> memberIds;

    @NotNull(message = "積點數量不可為空")
    @Min(value = 1, message = "積點數量必須大於 0")
    @Schema(description = "積點數量", example = "100")
    private Integer points;

    @NotBlank(message = "原因描述不可為空")
    @Size(max = 500, message = "原因描述最多 500 字")
    @Schema(description = "原因描述", example = "週年慶活動贈送")
    private String reason;
}
