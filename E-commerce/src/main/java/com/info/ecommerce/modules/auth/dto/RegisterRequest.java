package com.info.ecommerce.modules.auth.dto;

import com.info.ecommerce.modules.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "請輸入帳號")
    @Size(min = 3, max = 100, message = "帳號長度需為 3 到 100 個字元")
    private String username;

    @NotBlank(message = "請輸入 Email")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "請輸入密碼")
    @Size(min = 8, max = 100, message = "密碼長度需為 8 到 100 個字元")
    private String password;

    /**
     * 已忽略：公開註冊一律建立 CUSTOMER 帳號，員工帳號請由管理員透過 /api/users 建立
     */
    private Role role;
}
