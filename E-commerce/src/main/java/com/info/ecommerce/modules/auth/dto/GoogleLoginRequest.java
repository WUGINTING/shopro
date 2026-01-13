package com.info.ecommerce.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Google OAuth login requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleLoginRequest {

    @NotBlank(message = "Google ID Token is required")
    private String idToken;
}

