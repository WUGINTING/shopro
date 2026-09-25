package com.info.ecommerce.modules.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "優惠券")
public class CouponDTO {

    private Long id;

    @NotBlank(message = "請輸入優惠券代碼")
    @Size(max = 50, message = "代碼長度不可超過 50 字")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "代碼只能包含英文、數字、底線與連字號")
    @Schema(description = "代碼（不分大小寫）", example = "WELCOME100")
    private String code;

    @NotBlank(message = "請輸入優惠券名稱")
    @Size(max = 200, message = "名稱長度不可超過 200 字")
    private String name;

    @NotBlank(message = "請選擇折扣類型")
    @Pattern(regexp = "^(PERCENTAGE|FIXED|FREE_SHIPPING)$", message = "折扣類型不正確")
    @Schema(description = "PERCENTAGE / FIXED / FREE_SHIPPING")
    private String type;

    @DecimalMin(value = "0", message = "折扣不可小於 0")
    @Schema(description = "PERCENTAGE：折扣百分比（10 = 打 9 折）；FIXED：折抵金額")
    private BigDecimal discountValue;

    @DecimalMin(value = "0", message = "最低消費不可小於 0")
    private BigDecimal minPurchaseAmount;

    @DecimalMin(value = "0", message = "折抵上限不可小於 0")
    private BigDecimal maxDiscountAmount;

    @NotNull(message = "請輸入總數量")
    @Min(value = 1, message = "總數量至少為 1")
    private Integer totalCount;

    private Integer usedCount;

    @NotNull(message = "請選擇生效日期")
    private LocalDate validFrom;

    @NotNull(message = "請選擇過期日期")
    private LocalDate validUntil;

    private Boolean enabled;

    @Schema(description = "是否公開顯示在前台優惠頁")
    private Boolean publicVisible;

    @Size(max = 500, message = "適用說明不可超過 500 字")
    private String applicable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
