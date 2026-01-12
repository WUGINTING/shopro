package com.info.ecommerce.modules.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付回調記錄 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "支付回調記錄資料傳輸物件")
public class PaymentCallbackLogDTO {

    @Schema(description = "記錄 ID")
    private Long id;

    @Schema(description = "支付閘道類型")
    private String gateway;

    @Schema(description = "訂單編號")
    private String orderNumber;

    @Schema(description = "交易 ID")
    private String transactionId;

    @Schema(description = "回調狀態")
    private String status;

    @Schema(description = "原始請求參數（JSON）")
    private String rawParams;

    @Schema(description = "解析後的響應數據（JSON）")
    private String parsedResponse;

    @Schema(description = "處理結果")
    private String processResult;

    @Schema(description = "錯誤訊息")
    private String errorMessage;

    @Schema(description = "請求 IP")
    private String requestIp;

    @Schema(description = "User-Agent")
    private String userAgent;

    @Schema(description = "處理時間（毫秒）")
    private Long processTimeMs;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}

