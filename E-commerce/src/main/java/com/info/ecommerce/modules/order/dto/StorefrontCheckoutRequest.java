package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 前台（訪客）結帳請求
 * 價格一律由後端依商品/規格重新計算，前端傳入的金額不被信任
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "前台結帳請求")
public class StorefrontCheckoutRequest {

    @NotBlank(message = "請輸入收件人姓名")
    @Size(max = 100, message = "姓名長度不可超過 100 字")
    @Schema(description = "收件人姓名", required = true)
    private String customerName;

    @NotBlank(message = "請輸入聯絡電話")
    @Pattern(regexp = "^09\\d{8}$", message = "請輸入正確的手機號碼格式 (09xxxxxxxx)")
    @Schema(description = "聯絡電話", required = true)
    private String customerPhone;

    @NotBlank(message = "請輸入電子郵件")
    @Email(message = "電子郵件格式不正確")
    @Size(max = 100, message = "電子郵件長度不可超過 100 字")
    @Schema(description = "電子郵件（查詢訂單時使用）", required = true)
    private String customerEmail;

    @Size(max = 500, message = "地址長度不可超過 500 字")
    @Schema(description = "收件地址（宅配時必填）")
    private String shippingAddress;

    @Size(max = 500, message = "備註長度不可超過 500 字")
    @Schema(description = "訂單備註")
    private String notes;

    @Schema(description = "配送方式：HOME_DELIVERY（宅配）/ STORE_PICKUP（門市自取），預設 HOME_DELIVERY")
    private String shippingMethod;

    @Schema(description = "付款方式：ECPAY（線上付款）/ COD（貨到付款），預設 ECPAY")
    private String paymentMethod;

    @Size(max = 50, message = "優惠券代碼長度不可超過 50 字")
    @Schema(description = "優惠券代碼（選填）")
    private String couponCode;

    @Schema(description = "同意接收優惠與新品通知 Email（勾選才會加入 EDM 名單）")
    private Boolean marketingOptIn;

    @Schema(description = "下單來源：STOREFRONT（前台商城，預設）/ ADMIN_STORE（後台 App 的顧客商城），決定付款完成後導回的頁面")
    private String channel;

    @NotEmpty(message = "購物車是空的")
    @Size(max = 100, message = "單筆訂單最多 100 項商品")
    @Valid
    @Schema(description = "購買品項", required = true)
    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "結帳試算請求")
    public static class QuoteRequest {

        @NotEmpty(message = "購物車是空的")
        @Size(max = 100, message = "單筆訂單最多 100 項商品")
        @Valid
        @Schema(description = "購買品項", required = true)
        private List<Item> items;

        @Schema(description = "配送方式：HOME_DELIVERY / STORE_PICKUP")
        private String shippingMethod;

        @Size(max = 50, message = "優惠券代碼長度不可超過 50 字")
        @Schema(description = "優惠券代碼（選填）")
        private String couponCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "購買品項")
    public static class Item {

        @NotNull(message = "商品 ID 不能為空")
        @Schema(description = "商品 ID", required = true)
        private Long productId;

        @Schema(description = "規格 ID（商品有規格時必填）")
        private Long specificationId;

        @NotNull(message = "數量不能為空")
        @Min(value = 1, message = "數量必須大於 0")
        @Max(value = 999, message = "單品項數量不可超過 999")
        @Schema(description = "數量", required = true)
        private Integer quantity;
    }
}
