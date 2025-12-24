package com.info.ecommerce.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "票券商品設定")
public class ProductVoucherDTO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @NotNull(message = "商品 ID 不可為空")
    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @Schema(description = "票券有效期開始", example = "2024-01-01T00:00:00")
    private LocalDateTime validFrom;

    @Schema(description = "票券有效期結束", example = "2024-12-31T23:59:59")
    private LocalDateTime validTo;

    @Schema(description = "票券使用說明", example = "本票券可於全台門市使用")
    private String usageInstructions;

    @Schema(description = "是否可退款", example = "false")
    private Boolean refundable;
}
