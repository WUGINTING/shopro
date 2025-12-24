package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "訂單折扣資料")
public class OrderDiscountDTO {

    @Schema(description = "折扣記錄 ID")
    private Long id;

    @NotNull(message = "訂單 ID 不能為空")
    @Schema(description = "訂單 ID", required = true)
    private Long orderId;

    @NotNull(message = "折扣類型不能為空")
    @Schema(description = "折扣類型", required = true)
    private String discountType;

    @Schema(description = "折扣代碼")
    private String discountCode;

    @NotNull(message = "折扣金額不能為空")
    @DecimalMin(value = "0.00", message = "折扣金額不能小於 0")
    @Schema(description = "折扣金額", required = true)
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.00", message = "折扣百分比不能小於 0")
    @Schema(description = "折扣百分比")
    private BigDecimal discountPercentage;

    @Schema(description = "折扣描述")
    private String description;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
