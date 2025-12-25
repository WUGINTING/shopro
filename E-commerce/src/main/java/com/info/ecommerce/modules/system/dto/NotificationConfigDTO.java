package com.info.ecommerce.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知設定 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "通知設定")
public class NotificationConfigDTO {

    @Schema(description = "設定 ID")
    private Long id;

    @NotBlank(message = "通知類型不能為空")
    @Schema(description = "通知類型", example = "EMAIL")
    private String notificationType;

    @NotNull(message = "啟用狀態不能為空")
    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "SMTP 主機", example = "smtp.gmail.com")
    private String smtpHost;

    @Schema(description = "SMTP 端口", example = "587")
    private Integer smtpPort;

    @Schema(description = "SMTP 用戶名")
    private String smtpUsername;

    @Schema(description = "SMTP 密碼")
    private String smtpPassword;

    @NotNull(message = "SSL 設定不能為空")
    @Schema(description = "是否使用 SSL", example = "true")
    private Boolean smtpUseSsl;

    @Email(message = "寄件人信箱格式不正確")
    @Schema(description = "寄件人信箱", example = "noreply@shopro.com")
    private String fromEmail;

    @Schema(description = "寄件人名稱", example = "Shopro 電商平台")
    private String fromName;

    @Schema(description = "簡訊服務商", example = "TWILIO")
    private String smsProvider;

    @Schema(description = "簡訊 API 金鑰")
    private String smsApiKey;

    @Schema(description = "簡訊 API 密鑰")
    private String smsApiSecret;

    @Schema(description = "簡訊發送號碼")
    private String smsFromNumber;

    @NotBlank(message = "事件類型不能為空")
    @Schema(description = "事件類型", example = "ORDER_CREATED")
    private String eventType;

    @Schema(description = "事件名稱", example = "訂單已創建")
    private String eventName;

    @Schema(description = "模板主題")
    private String templateSubject;

    @Schema(description = "模板內容")
    private String templateBody;

    @Schema(description = "觸發條件（JSON 格式）")
    private String triggerConditions;

    @NotNull(message = "立即發送設定不能為空")
    @Schema(description = "是否立即發送", example = "true")
    private Boolean sendImmediately;

    @Min(value = 0, message = "延遲時間不能小於 0")
    @Schema(description = "延遲時間（分鐘）", example = "5")
    private Integer delayMinutes;

    @NotNull(message = "批次發送設定不能為空")
    @Schema(description = "是否允許批次發送", example = "false")
    private Boolean allowBatchSend;

    @Min(value = 1, message = "批次大小至少 1")
    @Schema(description = "批次大小", example = "100")
    private Integer batchSize;

    @NotNull(message = "最大重試次數不能為空")
    @Min(value = 0, message = "最大重試次數不能小於 0")
    @Schema(description = "最大重試次數", example = "3")
    private Integer maxRetries;

    @Min(value = 1, message = "重試間隔至少 1 分鐘")
    @Schema(description = "重試間隔（分鐘）", example = "5")
    private Integer retryIntervalMinutes;

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
