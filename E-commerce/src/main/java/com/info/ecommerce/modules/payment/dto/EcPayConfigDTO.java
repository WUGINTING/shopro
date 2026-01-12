package com.info.ecommerce.modules.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ECPay 配置 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "ECPay 配置資料傳輸物件")
public class EcPayConfigDTO {

    @Schema(description = "配置 ID")
    private Long id;

    @NotBlank(message = "商店代號不能為空")
    @Schema(description = "ECPay 商店代號 (MerchantID)")
    private String merchantId;

    @NotBlank(message = "HashKey 不能為空")
    @Schema(description = "ECPay HashKey")
    private String hashKey;

    @NotBlank(message = "HashIV 不能為空")
    @Schema(description = "ECPay HashIV")
    private String hashIV;

    @NotBlank(message = "API URL 不能為空")
    @Schema(description = "ECPay API Base URL")
    private String apiUrl;

    @Schema(description = "付款完成返回 URL")
    private String returnUrl;

    @NotBlank(message = "通知 URL 不能為空")
    @Schema(description = "付款結果通知 URL")
    private String notifyUrl;

    @NotNull(message = "測試模式設定不能為空")
    @Schema(description = "是否為測試環境")
    private Boolean sandbox;

    @NotNull(message = "啟用狀態不能為空")
    @Schema(description = "是否啟用")
    private Boolean enabled;

    @Schema(description = "備註說明")
    private String description;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}

