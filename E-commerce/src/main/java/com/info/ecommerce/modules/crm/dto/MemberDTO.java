package com.info.ecommerce.modules.crm.dto;

import com.info.ecommerce.modules.crm.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "會員資訊")
public class MemberDTO {

    @Schema(description = "會員 ID", example = "1")
    private Long id;

    @NotBlank(message = "會員姓名不可為空")
    @Size(max = 100, message = "會員姓名最多 100 字")
    @Schema(description = "會員姓名", example = "王小明")
    private String name;

    @NotBlank(message = "電子郵件不可為空")
    @Email(message = "電子郵件格式不正確")
    @Size(max = 100, message = "電子郵件最多 100 字")
    @Schema(description = "電子郵件", example = "wang@example.com")
    private String email;

    @Size(max = 20, message = "手機號碼最多 20 字")
    @Schema(description = "手機號碼", example = "0912345678")
    private String phone;

    @Schema(description = "生日", example = "1990-01-01")
    private LocalDate birthday;

    @Schema(description = "會員狀態", example = "ACTIVE")
    private MemberStatus status;

    @Schema(description = "會員等級 ID", example = "1")
    private Long levelId;

    @Schema(description = "總積點", example = "1000")
    private Integer totalPoints;

    @Schema(description = "可用積點", example = "800")
    private Integer availablePoints;

    @Size(max = 500, message = "地址最多 500 字")
    @Schema(description = "地址")
    private String address;

    @Size(max = 10, message = "郵遞區號最多 10 字")
    @Schema(description = "郵遞區號", example = "100")
    private String postalCode;

    @Pattern(regexp = "^[MFO]$", message = "性別必須為 M、F 或 O")
    @Schema(description = "性別 (M/F/O)", example = "M")
    private String gender;

    @Size(max = 500, message = "備註最多 500 字")
    @Schema(description = "備註")
    private String notes;

    @Schema(description = "註冊日期")
    private LocalDateTime registeredAt;

    @Schema(description = "最後登入時間")
    private LocalDateTime lastLoginAt;
}
