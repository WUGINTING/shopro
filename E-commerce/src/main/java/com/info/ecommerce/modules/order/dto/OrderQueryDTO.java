package com.info.ecommerce.modules.order.dto;

import com.info.ecommerce.modules.order.enums.OrderStatus;
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
@Schema(description = "訂單查詢條件")
public class OrderQueryDTO {

    @Schema(description = "訂單 ID")
    private Long orderId;

    @Schema(description = "訂單編號")
    private String orderNumber;

    @Schema(description = "客戶 ID")
    private Long customerId;

    @Schema(description = "客戶姓名")
    private String customerName;

    @Schema(description = "訂單狀態")
    private OrderStatus status;

    @Schema(description = "開始日期")
    private LocalDateTime startDate;

    @Schema(description = "結束日期")
    private LocalDateTime endDate;

    @Schema(description = "最小金額")
    private BigDecimal minAmount;

    @Schema(description = "最大金額")
    private BigDecimal maxAmount;

    @Schema(description = "是否暫存訂單")
    private Boolean isDraft;

    @Schema(description = "門市 ID")
    private Long storeId;

    @Schema(description = "頁碼", example = "0")
    private Integer page;

    @Schema(description = "每頁數量", example = "20")
    private Integer size;
}
