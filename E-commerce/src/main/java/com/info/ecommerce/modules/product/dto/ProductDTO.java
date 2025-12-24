package com.info.ecommerce.modules.product.dto;

import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "商品資訊")
public class ProductDTO {

    @Schema(description = "商品 ID", example = "1")
    private Long id;

    @NotBlank(message = "商品名稱不可為空")
    @Size(max = 200, message = "商品名稱最多 200 字")
    @Schema(description = "商品名稱", example = "經典白T恤")
    private String name;

    @Size(max = 50, message = "商品編號最多 50 字")
    @Schema(description = "商品編號", example = "SKU001")
    private String sku;

    @Schema(description = "商品描述", example = "100% 純棉材質，舒適透氣")
    private String description;

    @Schema(description = "商品分類 ID", example = "1")
    private Long categoryId;

    @Schema(description = "商品狀態", example = "ACTIVE")
    private ProductStatus status;

    @NotNull(message = "銷售模式不可為空")
    @Schema(description = "銷售模式", example = "NORMAL")
    private ProductSalesMode salesMode;

    @DecimalMin(value = "0.0", message = "基礎價格不可小於 0")
    @Schema(description = "基礎價格", example = "499.00")
    private BigDecimal basePrice;

    @DecimalMin(value = "0.0", message = "銷售價格不可小於 0")
    @Schema(description = "銷售價格", example = "399.00")
    private BigDecimal salePrice;

    @DecimalMin(value = "0.0", message = "成本價格不可小於 0")
    @Schema(description = "成本價格", example = "200.00")
    private BigDecimal costPrice;

    @Schema(description = "商品重量（克）", example = "200")
    private Integer weight;

    @Min(value = 1, message = "最小購買數量不可小於 1")
    @Schema(description = "最小購買數量", example = "1")
    private Integer minPurchaseQuantity;

    @Schema(description = "最大購買數量", example = "10")
    private Integer maxPurchaseQuantity;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "SEO 標題", example = "經典白T恤 - 100% 純棉")
    private String metaTitle;

    @Schema(description = "SEO 描述")
    private String metaDescription;

    @Schema(description = "SEO 關鍵字")
    private String metaKeywords;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "商品圖片列表")
    private List<ProductImageDTO> images;

    @Schema(description = "商品標籤列表")
    private List<String> tags;

    @Schema(description = "商品規格列表")
    private List<ProductSpecificationDTO> specifications;
}
