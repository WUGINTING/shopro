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

import java.time.LocalDateTime;

/**
 * DTO for User entity - used for account management
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "使用者名稱為必填")
    @Size(min = 3, max = 100, message = "使用者名稱長度必須在 3 到 100 字元之間")
    private String username;

    @NotBlank(message = "Email 為必填")
    @Email(message = "Email 格式不正確")
    private String email;

    // Password is optional for updates, required for creation
    @Size(min = 6, message = "密碼長度至少需要 6 個字元")
    private String password;

    @NotNull(message = "角色為必填")
    private Role role;

    private Boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
