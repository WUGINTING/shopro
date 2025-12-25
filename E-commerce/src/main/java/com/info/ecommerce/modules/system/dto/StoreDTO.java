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
 * 門市 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "門市資訊")
public class StoreDTO {

    @Schema(description = "門市 ID")
    private Long id;

    @NotBlank(message = "門市代碼不能為空")
    @Size(max = 50, message = "門市代碼長度不能超過 50")
    @Schema(description = "門市代碼", example = "TPE001")
    private String storeCode;

    @NotBlank(message = "門市名稱不能為空")
    @Size(max = 200, message = "門市名稱長度不能超過 200")
    @Schema(description = "門市名稱", example = "台北信義門市")
    private String storeName;

    @Schema(description = "聯絡電話", example = "02-1234-5678")
    private String phoneNumber;

    @Email(message = "信箱格式不正確")
    @Schema(description = "信箱", example = "tpe001@shopro.com")
    private String email;

    @Schema(description = "地址", example = "台北市信義區信義路五段 7 號")
    private String address;

    @Schema(description = "城市", example = "台北市")
    private String city;

    @Schema(description = "行政區", example = "信義區")
    private String district;

    @Schema(description = "郵遞區號", example = "110")
    private String postalCode;

    @DecimalMin(value = "-90.0000000", message = "緯度範圍不正確")
    @DecimalMax(value = "90.0000000", message = "緯度範圍不正確")
    @Schema(description = "緯度", example = "25.0339031")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0000000", message = "經度範圍不正確")
    @DecimalMax(value = "180.0000000", message = "經度範圍不正確")
    @Schema(description = "經度", example = "121.5644722")
    private BigDecimal longitude;

    @Schema(description = "營業時間（JSON 格式）", example = "{\"monday\":\"09:00-18:00\"}")
    private String businessHours;

    @Schema(description = "休息日（JSON 陣列）", example = "[\"sunday\",\"holiday\"]")
    private String closedDays;

    @NotNull(message = "啟用狀態不能為空")
    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @NotNull(message = "取貨設定不能為空")
    @Schema(description = "是否允許門市取貨", example = "true")
    private Boolean allowPickup;

    @NotNull(message = "付款設定不能為空")
    @Schema(description = "是否允許門市付款", example = "true")
    private Boolean allowPayment;

    @Schema(description = "倉庫 ID")
    private Long warehouseId;

    @Schema(description = "店長 ID")
    private Long managerId;

    @Schema(description = "店長姓名", example = "王小明")
    private String managerName;

    @Schema(description = "門市圖片（JSON 陣列）")
    private String images;

    @Schema(description = "門市描述")
    private String description;

    @Min(value = 1, message = "取貨準備時間至少 1 小時")
    @Schema(description = "取貨準備時間（小時）", example = "2")
    private Integer pickupPreparationHours;

    @Schema(description = "取貨說明")
    private String pickupInstructions;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "備註")
    private String notes;

    @Schema(description = "創建時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;

    @Schema(description = "更新者")
    private String updatedBy;
}
