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

    @Schema(description = "折扣金額（促銷、優惠券或會員等級，取最優惠者）")
    private BigDecimal discountAmount;

    @Schema(description = "套用的折扣明細（含免運）")
    private List<Discount> discounts;

    @Schema(description = "實際套用的優惠券代碼（未套用為 null）")
    private String couponCode;

    @Schema(description = "輸入的優惠券未套用時的原因")
    private String couponMessage;

    @Schema(description = "訂單總額 = 小計 - 折扣 + 運費")
    private BigDecimal totalAmount;

    @Schema(description = "配送方式")
    private String shippingMethod;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "套用的折扣")
    public static class Discount {
        @Schema(description = "PROMOTION / COUPON / MEMBER_LEVEL / FREE_SHIPPING")
        private String type;
        @Schema(description = "名稱")
        private String name;
        @Schema(description = "優惠券代碼")
        private String code;
        @Schema(description = "折抵金額（免運為 0）")
        private BigDecimal amount;
    }

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
