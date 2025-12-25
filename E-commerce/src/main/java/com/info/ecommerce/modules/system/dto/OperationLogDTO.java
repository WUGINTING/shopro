package com.info.ecommerce.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日誌 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "操作日誌")
public class OperationLogDTO {

    @Schema(description = "日誌 ID")
    private Long id;

    @Schema(description = "用戶 ID")
    private Long userId;

    @Schema(description = "用戶名稱")
    private String username;

    @Schema(description = "操作類型", example = "CREATE")
    private String operationType;

    @Schema(description = "操作模組", example = "PRODUCT")
    private String module;

    @Schema(description = "操作描述", example = "創建商品")
    private String description;

    @Schema(description = "目標類型", example = "Product")
    private String targetType;

    @Schema(description = "目標 ID")
    private Long targetId;

    @Schema(description = "目標名稱")
    private String targetName;

    @Schema(description = "請求方法", example = "POST")
    private String requestMethod;

    @Schema(description = "請求 URL", example = "/api/products")
    private String requestUrl;

    @Schema(description = "請求參數")
    private String requestParams;

    @Schema(description = "請求內容")
    private String requestBody;

    @Schema(description = "回應狀態碼", example = "200")
    private Integer responseStatus;

    @Schema(description = "回應內容")
    private String responseBody;

    @Schema(description = "IP 地址", example = "192.168.1.1")
    private String ipAddress;

    @Schema(description = "用戶代理")
    private String userAgent;

    @Schema(description = "執行時間（毫秒）", example = "150")
    private Long executionTime;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "錯誤訊息")
    private String errorMessage;

    @Schema(description = "是否為敏感操作", example = "false")
    private Boolean sensitive;

    @Schema(description = "備註")
    private String notes;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;
}
