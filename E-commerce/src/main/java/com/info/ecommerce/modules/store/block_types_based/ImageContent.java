package com.info.ecommerce.modules.store.block_types_based;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// --- 6. IMAGE (單張圖片) ---
@Data
public class ImageContent {
    @NotBlank(message = "圖片路徑不可為空")
    private String imageUrl;        // [cite: 89]
    private String linkUrl;         // [cite: 90]
    private String altText;         // [cite: 91]
    private String width;           // [cite: 92]
}
