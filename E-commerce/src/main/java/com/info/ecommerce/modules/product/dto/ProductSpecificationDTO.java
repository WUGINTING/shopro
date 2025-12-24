package com.info.ecommerce.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "商品規格")
public class ProductSpecificationDTO {

    @Schema(description = "規格 ID", example = "1")
    private Long id;

    @NotNull(message = "商品 ID 不可為空")
    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @NotBlank(message = "規格名稱不可為空")
    @Size(max = 200, message = "規格名稱最多 200 字")
    @Schema(description = "規格名稱", example = "顏色:紅色,尺寸:L")
    private String specName;

    @Size(max = 50, message = "規格 SKU 最多 50 字")
    @Schema(description = "規格 SKU", example = "SKU001-RED-L")
    private String sku;

    @DecimalMin(value = "0.0", message = "規格價格不可小於 0")
    @Schema(description = "規格價格", example = "399.00")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "規格成本不可小於 0")
    @Schema(description = "規格成本", example = "200.00")
    private BigDecimal cost;

    @Schema(description = "庫存數量", example = "100")
    private Integer stock;

    @Schema(description = "規格圖片", example = "/images/spec-red.jpg")
    private String image;

    @Schema(description = "重量（克）", example = "200")
    private Integer weight;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;
}
