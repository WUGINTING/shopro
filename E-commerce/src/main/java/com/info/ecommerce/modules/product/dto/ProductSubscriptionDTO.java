package com.info.ecommerce.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "訂閱商品設定")
public class ProductSubscriptionDTO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @NotNull(message = "商品 ID 不可為空")
    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @Schema(description = "訂閱週期（天數）", example = "30")
    private Integer subscriptionPeriodDays;

    @Schema(description = "訂閱週期類型", example = "MONTHLY", allowableValues = {"DAILY", "WEEKLY", "MONTHLY"})
    private String periodType;

    @Schema(description = "最少訂閱期數", example = "3")
    private Integer minPeriods;

    @Schema(description = "最多訂閱期數", example = "12")
    private Integer maxPeriods;

    @Schema(description = "是否自動續訂", example = "true")
    private Boolean autoRenew;
}
