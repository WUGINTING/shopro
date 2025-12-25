package com.info.ecommerce.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @Size(min = 3, max = 100, message = "使用者名稱長度必須在 3 到 100 字元之間")
    private String username;

    @Email(message = "Email 格式不正確")
    private String email;

    @Size(min = 6, message = "密碼長度至少需要 6 個字元")
    private String currentPassword;

    @Size(min = 6, message = "新密碼長度至少需要 6 個字元")
    private String newPassword;
}
