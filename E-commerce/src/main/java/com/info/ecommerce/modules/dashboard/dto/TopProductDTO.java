package com.info.ecommerce.modules.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "熱銷商品")
public class TopProductDTO {

    @Schema(description = "商品 ID")
    private Long id;

    @Schema(description = "商品名稱")
    private String name;

    @Schema(description = "銷售數量")
    private Long salesCount;

    @Schema(description = "商品價格")
    private BigDecimal price;

    @Schema(description = "商品圖片 URL")
    private String imageUrl;
}
