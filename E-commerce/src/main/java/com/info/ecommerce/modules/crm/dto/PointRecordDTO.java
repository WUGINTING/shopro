package com.info.ecommerce.modules.crm.dto;

import com.info.ecommerce.modules.crm.enums.PointType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "積點紀錄資訊")
public class PointRecordDTO {

    @Schema(description = "積點紀錄 ID", example = "1")
    private Long id;

    @NotNull(message = "會員 ID 不可為空")
    @Schema(description = "會員 ID", example = "1")
    private Long memberId;

    @NotNull(message = "積點類型不可為空")
    @Schema(description = "積點類型", example = "EARN")
    private PointType pointType;

    @NotNull(message = "積點數量不可為空")
    @Schema(description = "積點數量（正數為增加，負數為減少）", example = "100")
    private Integer points;

    @Schema(description = "變更後餘額", example = "1000")
    private Integer balanceAfter;

    @Schema(description = "關聯訂單 ID", example = "123")
    private Long orderId;

    @Size(max = 500, message = "原因描述最多 500 字")
    @Schema(description = "原因描述")
    private String reason;

    @Schema(description = "到期時間")
    private LocalDateTime expiresAt;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;
}
