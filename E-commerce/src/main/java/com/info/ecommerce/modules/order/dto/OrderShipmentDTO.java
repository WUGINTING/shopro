package com.info.ecommerce.modules.order.dto;

import com.info.ecommerce.modules.order.enums.ShippingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "物流資料傳輸物件")
public class OrderShipmentDTO {

    @Schema(description = "物流記錄 ID")
    private Long id;

    @NotNull(message = "訂單 ID 不能為空")
    @Schema(description = "訂單 ID", required = true)
    private Long orderId;

    @NotNull(message = "物流狀態不能為空")
    @Schema(description = "物流狀態", required = true)
    private ShippingStatus shippingStatus;

    @Schema(description = "物流公司")
    private String shippingCompany;

    @Schema(description = "物流單號")
    private String trackingNumber;

    @Schema(description = "出貨時間")
    private LocalDateTime shippedAt;

    @Schema(description = "送達時間")
    private LocalDateTime deliveredAt;

    @Schema(description = "收件人姓名")
    private String recipientName;

    @Schema(description = "收件人電話")
    private String recipientPhone;

    @Schema(description = "收件地址")
    private String recipientAddress;

    @Schema(description = "備註")
    private String notes;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
