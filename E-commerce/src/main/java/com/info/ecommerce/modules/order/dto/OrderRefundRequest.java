package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "訂單退款")
public class OrderRefundRequest {

    @DecimalMin(value = "0.01", message = "退款金額需大於 0")
    @Schema(description = "退款金額；未填為全額（訂單總額扣除已退款金額）")
    private BigDecimal amount;

    @NotBlank(message = "請填寫退款原因")
    @Size(max = 500, message = "退款原因不可超過 500 字")
    private String reason;

    @Schema(description = "商品已退回或尚未出貨，歸還庫存")
    private boolean restock;
}
