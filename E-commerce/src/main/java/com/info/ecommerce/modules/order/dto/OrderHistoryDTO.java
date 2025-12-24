package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "訂單歷史記錄")
public class OrderHistoryDTO {

    @Schema(description = "歷史記錄 ID")
    private Long id;

    @Schema(description = "訂單 ID")
    private Long orderId;

    @Schema(description = "操作類型")
    private String actionType;

    @Schema(description = "操作描述")
    private String actionDescription;

    @Schema(description = "操作前狀態")
    private String oldStatus;

    @Schema(description = "操作後狀態")
    private String newStatus;

    @Schema(description = "操作者 ID")
    private Long operatorId;

    @Schema(description = "操作者名稱")
    private String operatorName;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
