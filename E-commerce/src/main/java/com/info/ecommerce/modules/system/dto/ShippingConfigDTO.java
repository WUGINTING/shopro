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
 * 物流設定 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "物流設定")
public class ShippingConfigDTO {

    @Schema(description = "設定 ID")
    private Long id;

    @NotBlank(message = "物流商名稱不能為空")
    @Schema(description = "物流商名稱", example = "BLACK_CAT")
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

    @NotBlank(message = "配送方式不能為空")
    @Schema(description = "配送方式", example = "HOME_DELIVERY")
    private String shippingMethod;

    @Schema(description = "配送方式名稱", example = "宅配到府")
    private String shippingMethodName;

    @NotNull(message = "基本運費不能為空")
    @DecimalMin(value = "0.00", message = "基本運費不能小於 0")
    @Schema(description = "基本運費", example = "80.00")
    private BigDecimal baseShippingFee;

    @DecimalMin(value = "0.00", message = "免運門檻不能小於 0")
    @Schema(description = "免運門檻", example = "1000.00")
    private BigDecimal freeShippingThreshold;

    @DecimalMin(value = "0.00", message = "離島加價不能小於 0")
    @Schema(description = "離島加價", example = "100.00")
    private BigDecimal remoteAreaSurcharge;

    @Schema(description = "離島地區列表（JSON 或逗號分隔）")
    private String remoteAreaList;

    @DecimalMin(value = "0.00", message = "最大重量不能小於 0")
    @Schema(description = "最大重量（公斤）", example = "20.00")
    private BigDecimal maxWeight;

    @DecimalMin(value = "0.00", message = "最大體積不能小於 0")
    @Schema(description = "最大體積（立方公分）", example = "150.00")
    private BigDecimal maxVolume;

    @Min(value = 1, message = "預估配送天數至少 1 天")
    @Schema(description = "預估配送天數", example = "3")
    private Integer estimatedDeliveryDays;

    @Schema(description = "配送時段（JSON 或逗號分隔）")
    private String deliveryTimeSlots;

    @Schema(description = "可配送區域（JSON 或逗號分隔）")
    private String availableAreas;

    @Schema(description = "不可配送區域（JSON 或逗號分隔）")
    private String unavailableAreas;

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
