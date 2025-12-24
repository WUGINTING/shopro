package com.info.ecommerce.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "商品庫存")
public class ProductInventoryDTO {

    @Schema(description = "庫存 ID", example = "1")
    private Long id;

    @NotNull(message = "商品 ID 不可為空")
    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @Schema(description = "規格 ID", example = "1")
    private Long specificationId;

    @Schema(description = "倉庫 ID", example = "1")
    private Long warehouseId;

    @Schema(description = "可用庫存", example = "100")
    private Integer availableStock;

    @Schema(description = "鎖定庫存", example = "10")
    private Integer lockedStock;

    @Schema(description = "安全庫存量", example = "20")
    private Integer safetyStock;
}
