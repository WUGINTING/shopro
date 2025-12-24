package com.info.ecommerce.modules.store.block_types_based;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoContent {
    @NotBlank(message = "影片連結不可為空")
    private String videoUrl;        // [cite: 80]
    private Boolean autoPlay;       // [cite: 81]
    private Boolean muted;          // [cite: 82]
    private String aspectRatio;     // 16:9 [cite: 83]
}
