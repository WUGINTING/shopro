package com.info.ecommerce.modules.store.block_types_based;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

// --- 1. BANNER (輪播圖) ---
@Data
public class BannerContent {
    @NotEmpty(message = "輪播圖片不可為空")
    private List<Slide> slides; // [cite: 14, 30]
    private Boolean autoPlay; // [cite: 26, 34]
    private Integer interval; // [cite: 27, 35]

    @Data
    public static class Slide {
        private String imageUrl; // [cite: 16, 31]
        private String linkUrl;  // [cite: 17, 32]
        private String altText;  // [cite: 18, 33]
    }
}
