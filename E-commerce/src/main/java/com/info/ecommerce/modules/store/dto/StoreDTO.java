package com.info.ecommerce.modules.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "商店設定")
public class StoreDTO {

    @Schema(description = "商店 ID", example = "1")
    private Long id;

    @NotBlank(message = "商店名稱不可為空")
    @Size(max = 100, message = "商店名稱最多 100 字")
    @Schema(description = "商店名稱", example = "我的商店")
    private String name;

    @Schema(description = "Logo 圖片路徑", example = "/images/logo.png")
    private String logo;

    @Schema(description = "佈景等級", example = "GOLD", allowableValues = {"SILVER", "GOLD"})
    private String themeLevel;

    @Schema(description = "預設幣別", example = "TWD")
    private String defaultCurrency;

    @Schema(description = "電子發票啟用", example = "true")
    private Boolean invoiceEnabled;

    @Schema(description = "SEO 標題", example = "我的商店 - 最優質的購物體驗")
    private String metaTitle;

    @Schema(description = "SEO 描述", example = "提供最優質的商品與服務")
    private String metaDescription;

    @Schema(description = "SEO 關鍵字", example = "購物,電商,優惠")
    private String metaKeywords;

    @Schema(description = "營業開始時間", example = "09:00")
    private LocalTime businessStart;

    @Schema(description = "營業結束時間", example = "21:00")
    private LocalTime businessEnd;

    @Schema(description = "地址", example = "台北市信義區...")
    private String address;

    @Schema(description = "電話", example = "02-1234-5678")
    private String phone;

    @Schema(description = "Email", example = "store@example.com")
    private String email;
}
