package com.info.ecommerce.modules.auth.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.auth.dto.AuthResponse;
import com.info.ecommerce.modules.auth.dto.LoginRequest;
import com.info.ecommerce.modules.auth.dto.RegisterRequest;
import com.info.ecommerce.modules.auth.dto.UpdateProfileRequest;
import com.info.ecommerce.modules.auth.dto.UserDTO;
import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Get user from database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    /**
     * Get current user profile
     */
    public UserDTO getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("使用者不存在"));
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Update current user profile
     */
    @Transactional
    public UserDTO updateCurrentUserProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("使用者不存在"));

        // Update username if provided and different
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException("使用者名稱已存在：" + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email 已存在：" + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // Update password if both current and new passwords are provided
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException("目前密碼不正確");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        User updatedUser = userRepository.save(user);

        return UserDTO.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .enabled(updatedUser.getEnabled())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
    }

    /**
     * Google OAuth login/register
     * Validates Google ID Token and creates or logs in user
     */
    @Transactional
    public AuthResponse googleLogin(String idToken) {
        try {
            // Verify Google ID Token and get user info
            GoogleUserInfo googleUser = verifyGoogleToken(idToken);
            
            if (googleUser == null) {
                throw new BusinessException("無效的 Google Token");
            }

            // Check if user exists by email
            User user = userRepository.findByEmail(googleUser.getEmail())
                    .orElse(null);

            if (user == null) {
                // Create new user if doesn't exist
                user = User.builder()
                        .username(googleUser.getEmail().split("@")[0] + "_" + System.currentTimeMillis())
                        .email(googleUser.getEmail())
                        .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // Random password for OAuth users
                        .role(Role.CUSTOMER) // Default role for Google OAuth users
                        .enabled(true)
                        .build();
                user = userRepository.save(user);
            }

            // Generate JWT token
            String token = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        } catch (Exception e) {
            throw new BusinessException("Google 登入失敗: " + e.getMessage());
        }
    }

    /**
     * Verify Google ID Token and extract user information
     * Note: This is a simplified implementation. In production, you should use
     * Google's official library or properly verify the token signature.
     */
    private GoogleUserInfo verifyGoogleToken(String idToken) {
        try {
            // Decode JWT token (without verification for now)
            // In production, you should verify the token signature using Google's public keys
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            // Decode payload
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> claims = (java.util.Map<String, Object>) mapper.readValue(payload, java.util.Map.class);

            // Extract user info
            String email = (String) claims.get("email");
            String name = (String) claims.get("name");
            String picture = (String) claims.get("picture");

            if (email == null) {
                return null;
            }

            return GoogleUserInfo.builder()
                    .email(email)
                    .name(name != null ? name : email.split("@")[0])
                    .picture(picture)
                    .build();
        } catch (Exception e) {
            throw new BusinessException("無法解析 Google Token: " + e.getMessage());
        }
    }

    /**
     * Inner class for Google user information
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class GoogleUserInfo {
        private String email;
        private String name;
        private String picture;
    }
}
