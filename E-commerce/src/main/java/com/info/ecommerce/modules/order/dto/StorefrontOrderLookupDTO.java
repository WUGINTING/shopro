package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前台訂單查詢結果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "前台訂單查詢結果")
public class StorefrontOrderLookupDTO {

    @Schema(description = "訂單")
    private OrderDTO order;

    @Schema(description = "付款方式：ECPAY / COD（非前台建立的訂單為 null）")
    private String paymentMethod;

    @Schema(description = "是否可重新進行線上付款（線上付款且待付款）")
    private boolean canPayOnline;

    @Schema(description = "是否可由顧客自行取消（待付款且尚未出貨）")
    private boolean canCancel;

    @Schema(description = "物流資訊")
    private java.util.List<Shipment> shipments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "物流資訊")
    public static class Shipment {
        private String shippingCompany;
        private String trackingNumber;
        @Schema(description = "PENDING / SHIPPED / DELIVERED / RETURNED")
        private String status;
        private String statusLabel;
        private java.time.LocalDateTime shippedAt;
        private java.time.LocalDateTime deliveredAt;
    }
}
