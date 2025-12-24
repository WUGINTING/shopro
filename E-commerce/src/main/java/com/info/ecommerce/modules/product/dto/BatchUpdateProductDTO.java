package com.info.ecommerce.modules.product.dto;

import com.info.ecommerce.modules.product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "批次更新商品請求")
public class BatchUpdateProductDTO {

    @NotEmpty(message = "商品 ID 列表不可為空")
    @Schema(description = "商品 ID 列表", example = "[1, 2, 3]")
    private List<Long> productIds;

    @Schema(description = "更新狀態", example = "ACTIVE")
    private ProductStatus status;

    @Schema(description = "更新分類 ID", example = "1")
    private Long categoryId;

    @Schema(description = "更新價格", example = "399.00")
    private BigDecimal salePrice;

    @Schema(description = "更新排序", example = "10")
    private Integer sortOrder;

    @Schema(description = "更新啟用狀態", example = "true")
    private Boolean enabled;
}
