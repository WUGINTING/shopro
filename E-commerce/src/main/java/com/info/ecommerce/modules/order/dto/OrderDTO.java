package com.info.ecommerce.modules.order.dto;

import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PickupType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "訂單資料傳輸物件")
public class OrderDTO {

    @Schema(description = "訂單 ID")
    private Long id;

    @Schema(description = "訂單編號")
    private String orderNumber;

    @NotNull(message = "客戶 ID 不能為空")
    @Schema(description = "客戶 ID", required = true)
    private Long customerId;

    @Schema(description = "客戶姓名")
    private String customerName;

    @Schema(description = "客戶電話")
    private String customerPhone;

    @Email(message = "電子郵件格式不正確")
    @Schema(description = "客戶電子郵件")
    private String customerEmail;

    @NotNull(message = "訂單狀態不能為空")
    @Schema(description = "訂單狀態", required = true)
    private OrderStatus status;

    @NotNull(message = "取貨方式不能為空")
    @Schema(description = "取貨方式（O2O 支援）", required = true)
    private PickupType pickupType;

    @Schema(description = "門市 ID（用於門市取貨）")
    private Long storeId;

    @NotNull(message = "訂單金額小計不能為空")
    @DecimalMin(value = "0.00", message = "訂單金額小計不能小於 0")
    @Schema(description = "訂單金額小計", required = true)
    private BigDecimal subtotalAmount;

    @DecimalMin(value = "0.00", message = "折扣金額不能小於 0")
    @Schema(description = "折扣金額")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.00", message = "運費不能小於 0")
    @Schema(description = "運費")
    private BigDecimal shippingFee;

    @NotNull(message = "訂單總金額不能為空")
    @DecimalMin(value = "0.00", message = "訂單總金額不能小於 0")
    @Schema(description = "訂單總金額", required = true)
    private BigDecimal totalAmount;

    @Schema(description = "備註")
    private String notes;

    @Schema(description = "收貨地址")
    private String shippingAddress;

    @Schema(description = "是否暫存訂單")
    private Boolean isDraft;

    @Schema(description = "訂單項目列表")
    private List<OrderItemDTO> items;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;

    @Schema(description = "完成時間")
    private LocalDateTime completedAt;
}
