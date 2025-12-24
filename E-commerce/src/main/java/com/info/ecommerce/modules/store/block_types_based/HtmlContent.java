package com.info.ecommerce.modules.store.block_types_based;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HtmlContent {
    @NotBlank(message = "HTML 內容不可為空")
    private String html;            // [cite: 98]
    private String css;             // [cite: 99]
}
