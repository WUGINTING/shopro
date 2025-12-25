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
 * 系統基本設定 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "系統基本設定")
public class SystemConfigDTO {

    @Schema(description = "設定 ID")
    private Long id;

    @NotBlank(message = "網站名稱不能為空")
    @Size(max = 200, message = "網站名稱長度不能超過 200")
    @Schema(description = "網站名稱", example = "Shopro 電商平台")
    private String siteName;

    @Schema(description = "Logo URL", example = "https://example.com/logo.png")
    private String logoUrl;

    @Schema(description = "Favicon URL", example = "https://example.com/favicon.ico")
    private String faviconUrl;

    @Email(message = "聯絡信箱格式不正確")
    @Schema(description = "聯絡信箱", example = "contact@shopro.com")
    private String contactEmail;

    @Schema(description = "聯絡電話", example = "02-1234-5678")
    private String contactPhone;

    @Schema(description = "地址", example = "台北市信義區信義路五段 7 號")
    private String address;

    @Schema(description = "SEO Meta Title", example = "Shopro - 您的線上購物首選")
    private String metaTitle;

    @Schema(description = "SEO Meta Description")
    private String metaDescription;

    @Schema(description = "SEO Meta Keywords")
    private String metaKeywords;

    @Schema(description = "營業時間", example = "週一至週五 09:00-18:00")
    private String businessHours;

    @Schema(description = "休息日", example = "週日、國定假日")
    private String closedDays;

    @NotBlank(message = "預設幣別不能為空")
    @Schema(description = "預設幣別", example = "TWD")
    private String defaultCurrency;

    @NotNull(message = "稅率不能為空")
    @DecimalMin(value = "0.00", message = "稅率不能小於 0")
    @DecimalMax(value = "100.00", message = "稅率不能大於 100")
    @Schema(description = "稅率（百分比）", example = "5.00")
    private BigDecimal taxRate;

    @Schema(description = "訂單編號前綴", example = "ORD")
    private String orderNumberPrefix;

    @NotNull(message = "訂單自動取消時間不能為空")
    @Min(value = 1, message = "訂單自動取消時間至少 1 分鐘")
    @Schema(description = "訂單自動取消時間（分鐘）", example = "30")
    private Integer autoOrderCancelMinutes;

    @NotBlank(message = "庫存扣減時機不能為空")
    @Schema(description = "庫存扣減時機", example = "PAYMENT_COMPLETED", allowableValues = {"ORDER_CREATED", "PAYMENT_COMPLETED", "SHIPPED"})
    private String stockDeductionTiming;

    @NotNull(message = "會員註冊審核設定不能為空")
    @Schema(description = "是否需要會員註冊審核", example = "false")
    private Boolean requireMemberApproval;

    @NotNull(message = "積點有效期限不能為空")
    @Min(value = 1, message = "積點有效期限至少 1 天")
    @Schema(description = "積點有效期限（天）", example = "365")
    private Integer pointsExpirationDays;

    @NotNull(message = "最小密碼長度不能為空")
    @Min(value = 6, message = "最小密碼長度至少 6")
    @Schema(description = "最小密碼長度", example = "8")
    private Integer minPasswordLength;

    @Schema(description = "密碼正則表達式", example = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    private String passwordRegexPattern;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;

    @Schema(description = "更新者")
    private String updatedBy;
}
