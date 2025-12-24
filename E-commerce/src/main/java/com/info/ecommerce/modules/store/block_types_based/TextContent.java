package com.info.ecommerce.modules.store.block_types_based;

import lombok.Data;

@Data
public class TextContent {
    private String text;            // [cite: 70]
    private String alignment;       // left, center, right [cite: 71]
    private Integer fontSize;       // [cite: 72]
    private String fontColor;       // [cite: 73]
    private String backgroundColor; // [cite: 74]
}
