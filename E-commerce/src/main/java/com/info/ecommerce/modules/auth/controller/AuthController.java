package com.info.ecommerce.modules.auth.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.auth.dto.AuthResponse;
import com.info.ecommerce.modules.auth.dto.LoginRequest;
import com.info.ecommerce.modules.auth.dto.RegisterRequest;
import com.info.ecommerce.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "身份驗證", description = "用戶註冊和登錄")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "註冊新用戶", description = "創建新用戶帳戶並返回JWT令牌")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("註冊成功", authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "用戶登錄", description = "驗證憑據並返回JWT令牌")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("登錄成功", authService.login(request));
    }
}
