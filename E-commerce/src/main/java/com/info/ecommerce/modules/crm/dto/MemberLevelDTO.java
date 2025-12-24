package com.info.ecommerce.modules.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "會員等級資訊")
public class MemberLevelDTO {

    @Schema(description = "等級 ID", example = "1")
    private Long id;

    @NotBlank(message = "等級名稱不可為空")
    @Size(max = 50, message = "等級名稱最多 50 字")
    @Schema(description = "等級名稱", example = "黃金會員")
    private String name;

    @NotNull(message = "等級順序不可為空")
    @Min(value = 1, message = "等級順序必須大於 0")
    @Schema(description = "等級順序", example = "1")
    private Integer levelOrder;

    @DecimalMin(value = "0.0", message = "最低消費金額不可小於 0")
    @Schema(description = "所需最低消費金額", example = "10000.00")
    private BigDecimal minSpendAmount;

    @DecimalMin(value = "0.0", message = "折扣率不可小於 0")
    @DecimalMax(value = "1.0", message = "折扣率不可大於 1")
    @Schema(description = "折扣率 (0.0 - 1.0)", example = "0.95")
    private BigDecimal discountRate;

    @DecimalMin(value = "0.0", message = "積點倍率不可小於 0")
    @Schema(description = "積點倍率", example = "1.5")
    private BigDecimal pointsMultiplier;

    @Size(max = 500, message = "等級描述最多 500 字")
    @Schema(description = "等級描述")
    private String description;

    @Size(max = 500, message = "等級圖示 URL 最多 500 字")
    @Schema(description = "等級圖示 URL")
    private String iconUrl;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;
}
