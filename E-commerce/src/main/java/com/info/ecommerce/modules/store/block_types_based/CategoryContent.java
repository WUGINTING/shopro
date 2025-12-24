package com.info.ecommerce.modules.store.block_types_based;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CategoryContent {
    @NotEmpty(message = "分類 ID 列表不可為空")
    private List<Long> categoryIds; // [cite: 56, 62]
    private String displayStyle;    // icon, image, text [cite: 57, 63]
    private Integer columns;        // [cite: 58, 64]
    private Boolean showCount;      // [cite: 59, 65]
}
