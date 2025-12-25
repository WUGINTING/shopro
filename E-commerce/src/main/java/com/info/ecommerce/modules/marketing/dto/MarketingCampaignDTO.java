package com.info.ecommerce.modules.marketing.dto;

import com.info.ecommerce.modules.marketing.enums.CampaignStatus;
import com.info.ecommerce.modules.marketing.enums.CampaignType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "營銷活動資訊")
public class MarketingCampaignDTO {

    @Schema(description = "活動 ID", example = "1")
    private Long id;

    @NotBlank(message = "活動名稱不可為空")
    @Size(max = 200, message = "活動名稱最多 200 字")
    @Schema(description = "活動名稱", example = "雙11購物節")
    private String name;

    @Size(max = 1000, message = "活動描述最多 1000 字")
    @Schema(description = "活動描述", example = "全館商品8折優惠")
    private String description;

    @NotNull(message = "活動類型不可為空")
    @Schema(description = "活動類型", example = "DISCOUNT")
    private CampaignType type;

    @NotNull(message = "活動狀態不可為空")
    @Schema(description = "活動狀態", example = "ACTIVE")
    private CampaignStatus status;

    @NotNull(message = "開始日期不可為空")
    @Schema(description = "開始日期", example = "2024-11-11")
    private LocalDate startDate;

    @NotNull(message = "結束日期不可為空")
    @Schema(description = "結束日期", example = "2024-11-12")
    private LocalDate endDate;

    @Schema(description = "折扣率 (百分比)", example = "20.00")
    private BigDecimal discountRate;

    @Schema(description = "折扣金額", example = "100.00")
    private BigDecimal discountAmount;

    @Schema(description = "最低購買金額", example = "500.00")
    private BigDecimal minPurchaseAmount;

    @Size(max = 500, message = "目標客群最多 500 字")
    @Schema(description = "目標客群", example = "所有會員")
    private String targetAudience;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
