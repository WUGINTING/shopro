package com.info.ecommerce.modules.order.dto;

import com.info.ecommerce.modules.order.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "批量更新訂單")
public class BatchOrderUpdateDTO {

    @NotEmpty(message = "訂單 ID 列表不能為空")
    @Schema(description = "訂單 ID 列表", required = true)
    private List<Long> orderIds;

    @NotNull(message = "目標狀態不能為空")
    @Schema(description = "目標狀態", required = true)
    private OrderStatus targetStatus;

    @Schema(description = "操作者 ID")
    private Long operatorId;

    @Schema(description = "操作者名稱")
    private String operatorName;

    @Schema(description = "備註")
    private String notes;
}
