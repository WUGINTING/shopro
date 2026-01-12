package com.info.ecommerce.modules.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "促銷活動資訊")
public class PromotionDTO {

    @Schema(description = "活動 ID", example = "1")
    private Long id;

    @NotBlank(message = "活動名稱不可為空")
    @Size(max = 200, message = "活動名稱最多 200 字")
    @Schema(description = "活動名稱", example = "雙11購物節")
    private String name;

    @Size(max = 1000, message = "活動描述最多 1000 字")
    @Schema(description = "活動描述", example = "全館商品8折優惠")
    private String description;

    @NotBlank(message = "活動類型不可為空")
    @Schema(description = "活動類型", example = "DISCOUNT")
    private String type; // DISCOUNT, FULL_SHOP, FREE_SHIPPING, BUY_GIFT

    @Schema(description = "折扣類型", example = "PERCENTAGE")
    private String discountType; // PERCENTAGE, FIXED

    @Schema(description = "折扣值", example = "20.00")
    private BigDecimal discountValue;

    @Schema(description = "最低購買金額", example = "500.00")
    private BigDecimal minPurchaseAmount;

    @Schema(description = "最大折扣金額", example = "1000.00")
    private BigDecimal maxDiscountAmount;

    @NotNull(message = "開始日期不可為空")
    @Schema(description = "開始日期", example = "2024-11-11")
    private LocalDate startDate;

    @NotNull(message = "結束日期不可為空")
    @Schema(description = "結束日期", example = "2024-11-12")
    private LocalDate endDate;

    @NotNull(message = "是否啟用不可為空")
    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @NotNull(message = "優先級不可為空")
    @Schema(description = "優先級", example = "1")
    private Integer priority;

    @Size(max = 500, message = "條件描述最多 500 字")
    @Schema(description = "條件描述", example = "滿額即享折扣")
    private String conditions;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}

