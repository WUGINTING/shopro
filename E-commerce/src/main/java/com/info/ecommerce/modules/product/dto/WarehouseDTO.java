package com.info.ecommerce.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "倉庫")
public class WarehouseDTO {

    @Schema(description = "倉庫 ID", example = "1")
    private Long id;

    @NotBlank(message = "倉庫名稱不可為空")
    @Size(max = 100, message = "倉庫名稱最多 100 字")
    @Schema(description = "倉庫名稱", example = "台北倉庫")
    private String name;

    @Size(max = 50, message = "倉庫編號最多 50 字")
    @Schema(description = "倉庫編號", example = "WH001")
    private String code;

    @Size(max = 500, message = "倉庫地址最多 500 字")
    @Schema(description = "倉庫地址", example = "台北市信義區...")
    private String address;

    @Size(max = 50, message = "聯絡人最多 50 字")
    @Schema(description = "聯絡人", example = "張三")
    private String contactPerson;

    @Size(max = 20, message = "聯絡電話最多 20 字")
    @Schema(description = "聯絡電話", example = "02-1234-5678")
    private String contactPhone;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "是否為預設倉庫", example = "true")
    private Boolean isDefault;
}
