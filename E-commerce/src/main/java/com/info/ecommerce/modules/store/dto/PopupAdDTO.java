package com.info.ecommerce.modules.store.dto;

import com.info.ecommerce.modules.store.enums.DisplayFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "彈跳廣告")
public class PopupAdDTO {

    @Schema(description = "廣告 ID", example = "1")
    private Long id;

    @NotBlank(message = "標題不可為空")
    @Schema(description = "廣告標題", example = "新年特賣")
    private String title;

    @Schema(description = "圖片網址", example = "/images/popup.jpg")
    private String imageUrl;

    @Schema(description = "點擊連結", example = "/promotion/newyear")
    private String linkUrl;

    @Schema(description = "開始時間", example = "2024-01-01T00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "結束時間", example = "2024-01-31T23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "顯示頻率", example = "ONCE_PER_DAY")
    private DisplayFrequency displayFrequency;
}
