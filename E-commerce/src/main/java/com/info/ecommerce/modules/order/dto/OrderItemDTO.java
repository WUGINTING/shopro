package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "訂單項目資料傳輸物件")
public class OrderItemDTO {

    @Schema(description = "訂單項目 ID")
    private Long id;

    @Schema(description = "訂單 ID")
    private Long orderId;

    @NotNull(message = "商品 ID 不能為空")
    @Schema(description = "商品 ID", required = true)
    private Long productId;

    @Schema(description = "商品名稱")
    private String productName;

    @Schema(description = "商品 SKU")
    private String productSku;

    @Schema(description = "商品規格")
    private String productSpec;

    @NotNull(message = "單價不能為空")
    @DecimalMin(value = "0.00", message = "單價不能小於 0")
    @Schema(description = "單價", required = true)
    private BigDecimal unitPrice;

    @NotNull(message = "數量不能為空")
    @Min(value = 1, message = "數量必須大於 0")
    @Schema(description = "數量", required = true)
    private Integer quantity;

    @Schema(description = "小計金額")
    private BigDecimal subtotalAmount;

    @Schema(description = "折扣金額")
    private BigDecimal discountAmount;

    @Schema(description = "實際金額")
    private BigDecimal actualAmount;
}
