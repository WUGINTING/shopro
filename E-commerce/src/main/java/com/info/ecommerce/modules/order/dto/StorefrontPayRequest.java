package com.info.ecommerce.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前台重新付款請求（以訂單編號 + 下單 Email 驗證身分）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "前台重新付款請求")
public class StorefrontPayRequest {

    @NotBlank(message = "請輸入訂單編號")
    private String orderNumber;

    @NotBlank(message = "請輸入電子郵件")
    @Email(message = "電子郵件格式不正確")
    private String email;

    @Schema(description = "STOREFRONT（預設）/ ADMIN_STORE：決定付款完成後導回的頁面")
    private String channel;
}
