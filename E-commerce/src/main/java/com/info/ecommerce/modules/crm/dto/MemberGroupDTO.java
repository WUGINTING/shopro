package com.info.ecommerce.modules.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "會員群組資訊")
public class MemberGroupDTO {

    @Schema(description = "群組 ID", example = "1")
    private Long id;

    @NotBlank(message = "群組名稱不可為空")
    @Size(max = 100, message = "群組名稱最多 100 字")
    @Schema(description = "群組名稱", example = "VIP 客戶群組")
    private String name;

    @Size(max = 500, message = "群組描述最多 500 字")
    @Schema(description = "群組描述")
    private String description;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;
}
