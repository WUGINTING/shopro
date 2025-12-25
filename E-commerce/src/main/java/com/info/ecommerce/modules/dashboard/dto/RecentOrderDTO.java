package com.info.ecommerce.modules.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "最近訂單")
public class RecentOrderDTO {

    @Schema(description = "訂單 ID")
    private Long id;

    @Schema(description = "訂單編號")
    private String orderNumber;

    @Schema(description = "訂單狀態")
    private String status;

    @Schema(description = "總金額")
    private BigDecimal totalAmount;

    @Schema(description = "客戶名稱")
    private String customerName;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
