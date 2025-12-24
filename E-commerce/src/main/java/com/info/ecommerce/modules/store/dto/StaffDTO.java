package com.info.ecommerce.modules.store.dto;

import com.info.ecommerce.modules.store.enums.StaffRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "員工資料")
public class StaffDTO {

    @Schema(description = "員工 ID", example = "1")
    private Long id;

    @NotBlank(message = "帳號不可為空")
    @Size(max = 50, message = "帳號最多 50 字")
    @Schema(description = "登入帳號", example = "staff001")
    private String account;

    @Schema(description = "密碼（新增時必填）", example = "password123")
    private String password;

    @NotBlank(message = "姓名不可為空")
    @Size(max = 50, message = "姓名最多 50 字")
    @Schema(description = "姓名", example = "王小明")
    private String name;

    @Schema(description = "電話", example = "0912-345-678")
    private String phone;

    @Schema(description = "Email", example = "staff@example.com")
    private String email;

    @NotNull(message = "角色不可為空")
    @Schema(description = "角色", example = "STAFF")
    private StaffRole role;

    @Schema(description = "帳號啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "建立時間", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "最後登入時間", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastLoginAt;
}
