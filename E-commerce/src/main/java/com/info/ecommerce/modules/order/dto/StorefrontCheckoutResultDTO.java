package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前台結帳結果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "前台結帳結果")
public class StorefrontCheckoutResultDTO {

    @Schema(description = "已建立的訂單")
    private OrderDTO order;

    @Schema(description = "付款方式：ECPAY / COD")
    private String paymentMethod;

    @Schema(description = "線上付款導向網址（ECPAY 時提供，前端需以 POST 表單導向）")
    private String paymentUrl;

    @Schema(description = "建立線上付款失敗時的錯誤訊息（訂單仍已建立，可稍後付款）")
    private String paymentError;
}
