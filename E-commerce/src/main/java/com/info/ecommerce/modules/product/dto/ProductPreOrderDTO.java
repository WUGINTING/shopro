package com.info.ecommerce.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "預購商品設定")
public class ProductPreOrderDTO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @NotNull(message = "商品 ID 不可為空")
    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @NotNull(message = "預購開始時間不可為空")
    @Schema(description = "預購開始時間", example = "2024-01-01T00:00:00")
    private LocalDateTime preOrderStartTime;

    @NotNull(message = "預購結束時間不可為空")
    @Schema(description = "預購結束時間", example = "2024-01-31T23:59:59")
    private LocalDateTime preOrderEndTime;

    @Schema(description = "預計出貨時間", example = "2024-02-15T00:00:00")
    private LocalDateTime estimatedShipTime;

    @Schema(description = "預購數量限制", example = "1000")
    private Integer quantityLimit;

    @Schema(description = "已預購數量", example = "500")
    private Integer orderedQuantity;
}
