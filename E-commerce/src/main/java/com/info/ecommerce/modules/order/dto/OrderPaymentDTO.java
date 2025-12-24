package com.info.ecommerce.modules.order.dto;

import com.info.ecommerce.modules.order.enums.PaymentStatus;
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
@Schema(description = "付款資料傳輸物件")
public class OrderPaymentDTO {

    @Schema(description = "付款記錄 ID")
    private Long id;

    @NotNull(message = "訂單 ID 不能為空")
    @Schema(description = "訂單 ID", required = true)
    private Long orderId;

    @NotNull(message = "付款狀態不能為空")
    @Schema(description = "付款狀態", required = true)
    private PaymentStatus paymentStatus;

    @Schema(description = "付款方式")
    private String paymentMethod;

    @NotNull(message = "付款金額不能為空")
    @DecimalMin(value = "0.00", message = "付款金額不能小於 0")
    @Schema(description = "付款金額", required = true)
    private BigDecimal paymentAmount;

    @Schema(description = "付款交易號")
    private String transactionId;

    @Schema(description = "付款時間")
    private LocalDateTime paymentTime;

    @DecimalMin(value = "0.00", message = "退款金額不能小於 0")
    @Schema(description = "退款金額")
    private BigDecimal refundAmount;

    @Schema(description = "退款時間")
    private LocalDateTime refundTime;

    @Schema(description = "備註")
    private String notes;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
