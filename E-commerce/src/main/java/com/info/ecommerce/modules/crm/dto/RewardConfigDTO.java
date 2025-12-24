package com.info.ecommerce.modules.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "獎勵設定資訊")
public class RewardConfigDTO {

    @Schema(description = "獎勵設定 ID", example = "1")
    private Long id;

    @NotBlank(message = "獎勵類型不可為空")
    @Pattern(regexp = "^(WELCOME|BIRTHDAY|REFERRAL)$", message = "獎勵類型必須為 WELCOME、BIRTHDAY 或 REFERRAL")
    @Schema(description = "獎勵類型（WELCOME: 入會禮, BIRTHDAY: 生日禮, REFERRAL: 推薦回饋）", example = "WELCOME")
    private String rewardType;

    @NotBlank(message = "獎勵名稱不可為空")
    @Size(max = 100, message = "獎勵名稱最多 100 字")
    @Schema(description = "獎勵名稱", example = "新會員歡迎禮")
    private String name;

    @Size(max = 500, message = "獎勵描述最多 500 字")
    @Schema(description = "獎勵描述")
    private String description;

    @Min(value = 0, message = "獎勵積點不可小於 0")
    @Schema(description = "獎勵積點", example = "100")
    private Integer rewardPoints;

    @Schema(description = "優惠券 ID", example = "1")
    private Long couponId;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Min(value = 1, message = "有效天數必須大於 0")
    @Schema(description = "有效天數（生日禮適用）", example = "30")
    private Integer validDays;
}
