package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 前台結帳試算結果（依後端價格與物流設定計算）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "前台結帳試算結果")
public class StorefrontQuoteDTO {

    @Schema(description = "品項明細")
    private List<Line> lines;

    @Schema(description = "商品小計")
    private BigDecimal subtotalAmount;

    @Schema(description = "運費")
    private BigDecimal shippingFee;

    @Schema(description = "免運門檻（null 表示無免運門檻）")
    private BigDecimal freeShippingThreshold;

    @Schema(description = "訂單總額")
    private BigDecimal totalAmount;

    @Schema(description = "配送方式")
    private String shippingMethod;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "試算品項")
    public static class Line {

        private Long productId;

        private Long specificationId;

        private String productName;

        private String specName;

        private String sku;

        private BigDecimal unitPrice;

        private Integer quantity;

        private BigDecimal subtotalAmount;
    }
}
