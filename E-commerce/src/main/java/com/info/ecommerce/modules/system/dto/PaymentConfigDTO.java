package com.info.ecommerce.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 金流設定 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "金流設定")
public class PaymentConfigDTO {

    @Schema(description = "設定 ID")
    private Long id;

    @NotBlank(message = "金流商名稱不能為空")
    @Schema(description = "金流商名稱", example = "ECPAY")
    private String providerName;

    @NotNull(message = "啟用狀態不能為空")
    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "API 金鑰")
    private String apiKey;

    @Schema(description = "API 密鑰")
    private String apiSecret;

    @Schema(description = "API 端點")
    private String apiEndpoint;

    @Schema(description = "商店 ID")
    private String merchantId;

    @NotBlank(message = "付款方式不能為空")
    @Schema(description = "付款方式", example = "CREDIT_CARD")
    private String paymentMethod;

    @Schema(description = "付款方式名稱", example = "信用卡")
    private String paymentMethodName;

    @NotNull(message = "手續費設定不能為空")
    @Schema(description = "是否收取手續費", example = "true")
    private Boolean chargeTransactionFee;

    @DecimalMin(value = "0.00", message = "手續費率不能小於 0")
    @DecimalMax(value = "100.00", message = "手續費率不能大於 100")
    @Schema(description = "手續費率（百分比）", example = "2.50")
    private BigDecimal transactionFeeRate;

    @DecimalMin(value = "0.00", message = "固定手續費不能小於 0")
    @Schema(description = "固定手續費", example = "10.00")
    private BigDecimal transactionFeeFixed;

    @NotNull(message = "自動退款設定不能為空")
    @Schema(description = "是否啟用自動退款", example = "false")
    private Boolean autoRefundEnabled;

    @Min(value = 1, message = "自動退款天數至少 1 天")
    @Schema(description = "自動退款天數", example = "7")
    private Integer autoRefundDays;

    @NotNull(message = "退款審核設定不能為空")
    @Schema(description = "是否需要退款審核", example = "true")
    private Boolean requireRefundApproval;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @NotNull(message = "測試模式設定不能為空")
    @Schema(description = "是否為測試模式", example = "true")
    private Boolean testMode;

    @Schema(description = "備註")
    private String notes;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;

    @Schema(description = "更新者")
    private String updatedBy;
}
