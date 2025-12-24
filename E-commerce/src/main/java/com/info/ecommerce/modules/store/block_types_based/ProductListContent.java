package com.info.ecommerce.modules.store.block_types_based;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

// --- 2. PRODUCT_LIST (商品列表) ---
@Data
public class ProductListContent {
    @NotEmpty(message = "商品 ID 列表不可為空")
    private List<Long> productIds; // [cite: 40, 47]
    private String displayStyle;    // grid, list, carousel [cite: 41, 48]
    @Min(2) @Max(6)
    private Integer columns;        // 每行 2-6 個 [cite: 42, 49]
    private Boolean showPrice;     // [cite: 43, 50]
    private Boolean showAddToCart; // [cite: 44, 51]
}
