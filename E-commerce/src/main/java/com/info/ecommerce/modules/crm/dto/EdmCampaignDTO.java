package com.info.ecommerce.modules.crm.dto;

import com.info.ecommerce.modules.crm.enums.EdmStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "EDM 電子報活動資訊")
public class EdmCampaignDTO {

    @Schema(description = "活動 ID", example = "1")
    private Long id;

    @NotBlank(message = "活動名稱不可為空")
    @Size(max = 200, message = "活動名稱最多 200 字")
    @Schema(description = "活動名稱", example = "週年慶促銷活動")
    private String name;

    @NotBlank(message = "郵件主旨不可為空")
    @Size(max = 200, message = "郵件主旨最多 200 字")
    @Schema(description = "郵件主旨", example = "週年慶優惠，全館 8 折起！")
    private String subject;

    @Schema(description = "郵件內容")
    private String content;

    @Schema(description = "EDM 狀態", example = "DRAFT")
    private EdmStatus status;

    @Schema(description = "目標群組 ID（可選，null 表示全部會員）", example = "1")
    private Long targetGroupId;

    @Schema(description = "排程發送時間")
    private LocalDateTime scheduledAt;

    @Schema(description = "實際發送時間")
    private LocalDateTime sentAt;

    @Schema(description = "總發送數", example = "1000")
    private Integer totalSent;

    @Schema(description = "成功數", example = "950")
    private Integer successCount;

    @Schema(description = "失敗數", example = "50")
    private Integer failureCount;
}
