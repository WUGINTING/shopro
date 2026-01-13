package com.info.ecommerce.modules.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "時間變更預覽資訊")
public class CalendarPreviewDTO {

    @Schema(description = "預覽時間點", example = "2024-12-15T12:00:00")
    private LocalDateTime previewTime;

    @Schema(description = "該時間點上架的商品列表")
    private List<ProductPreviewDTO> listedProducts;

    @Schema(description = "該時間點進行的活動列表")
    private List<ActivityPreviewDTO> activeActivities;

    @Schema(description = "預期影響說明", example = "預計有 10 個商品上架，3 個活動進行中")
    private String impactDescription;

    @Data
    @Schema(description = "商品預覽資訊")
    public static class ProductPreviewDTO {
        @Schema(description = "商品ID", example = "1")
        private Long productId;

        @Schema(description = "商品名稱", example = "商品A")
        private String productName;

        @Schema(description = "商品SKU", example = "SKU001")
        private String sku;

        @Schema(description = "庫存數量", example = "100")
        private Integer stock;
    }

    @Data
    @Schema(description = "活動預覽資訊")
    public static class ActivityPreviewDTO {
        @Schema(description = "活動ID", example = "1")
        private Long activityId;

        @Schema(description = "活動名稱", example = "雙12購物節")
        private String activityName;

        @Schema(description = "活動類型", example = "MARKETING_CAMPAIGN")
        private String activityType;
    }
}

