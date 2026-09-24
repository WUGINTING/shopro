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
}
