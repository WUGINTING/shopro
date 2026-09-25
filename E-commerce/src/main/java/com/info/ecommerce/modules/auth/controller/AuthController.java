package com.info.ecommerce.modules.auth.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.auth.dto.AuthResponse;
import com.info.ecommerce.modules.auth.dto.LoginRequest;
import com.info.ecommerce.modules.auth.dto.RegisterRequest;
import com.info.ecommerce.modules.auth.dto.UpdateProfileRequest;
import com.info.ecommerce.modules.auth.dto.UserDTO;
import com.info.ecommerce.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    private final com.info.ecommerce.modules.auth.service.EmailVerificationService emailVerificationService;
    private final com.info.ecommerce.modules.auth.service.CurrentUserService currentUserService;
    private final com.info.ecommerce.modules.auth.service.PasswordResetService passwordResetService;
    private final com.info.ecommerce.common.RateLimiter rateLimiter;

    @PostMapping("/register")
    @Operation(summary = "註冊新用戶", description = "創建新用戶帳戶並返回JWT令牌")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("註冊成功", authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "用戶登錄", description = "驗證憑據並返回JWT令牌")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                           jakarta.servlet.http.HttpServletRequest http) {
        // 只計算失敗次數（先佔用名額，登入成功再歸還，同時送出的大量猜測也不會超過上限）：
        // 同一帳號 + 同一來源失敗 10 次、同一來源失敗 50 次、同一帳號（來自各處）失敗 50 次後暫停 15 分鐘。
        // 帳號層級的上限較寬，單一攻擊者無法輕易鎖住他人帳號；重設密碼成功後會解除。
        String tooMany = "登入失敗次數過多，請 15 分鐘後再試，或使用「忘記密碼」重設";
        java.time.Duration window = java.time.Duration.ofMinutes(15);
        String client = com.info.ecommerce.common.RateLimiter.clientKey(http);
        String username = request.getUsername();
        java.util.List<String[]> reserved = new java.util.ArrayList<>();
        try {
            reserve(reserved, "login-user-client", username + "|" + client, 10, window, tooMany);
            reserve(reserved, "login-client", client, 50, window, tooMany);
            reserve(reserved, "login-user", username, 50, window, tooMany);
        } catch (com.info.ecommerce.common.exception.BusinessException e) {
            reserved.forEach(slot -> rateLimiter.release(slot[0], slot[1]));
            throw e;
        }
        AuthResponse response = authService.login(request);
        reserved.forEach(slot -> rateLimiter.release(slot[0], slot[1]));
        return ApiResponse.success("登錄成功", response);
    }

    private void reserve(java.util.List<String[]> reserved, String bucket, String key, int max,
                         java.time.Duration window, String message) {
        rateLimiter.check(bucket, key, max, window, message);
        reserved.add(new String[]{bucket, key});
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "取得個人資料", description = "取得當前登入使用者的個人資料")
    public ApiResponse<UserDTO> getProfile(Authentication authentication) {
        String username = authentication.getName();
        return ApiResponse.success("成功取得個人資料", authService.getCurrentUserProfile(username));
    }

    @PutMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "更新個人資料", description = "更新當前登入使用者的個人資料")
    public ApiResponse<UserDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        String username = authentication.getName();
        return ApiResponse.success("個人資料更新成功", authService.updateCurrentUserProfile(username, request));
    }

    @PostMapping("/google")
    @Operation(summary = "Google SSO 登入/註冊", description = "使用 Google ID Token 進行登入或註冊")
    public ApiResponse<AuthResponse> googleLogin(@Valid @RequestBody com.info.ecommerce.modules.auth.dto.GoogleLoginRequest request) {
        return ApiResponse.success("登入成功", authService.googleLogin(request.getIdToken()));
    }

    @PostMapping("/email-verification")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "寄送 Email 驗證信", description = "寄送驗證連結到目前登入帳號的 Email")
    public ApiResponse<Void> sendEmailVerification() {
        emailVerificationService.sendVerification(currentUserService.currentUser()
                .orElseThrow(() -> new com.info.ecommerce.common.exception.BusinessException("請先登入")));
        return ApiResponse.success("驗證信已寄出，請至信箱點擊連結完成驗證", null);
    }

    @PostMapping("/password-reset")
    @Operation(summary = "申請重設密碼", description = "寄送重設密碼連結到 Email（不透露 Email 是否已註冊）")
    public ApiResponse<Void> requestPasswordReset(@RequestBody java.util.Map<String, String> body,
                                                  jakarta.servlet.http.HttpServletRequest http) {
        String tooMany = "申請次數過多，請稍後再試";
        String client = com.info.ecommerce.common.RateLimiter.clientKey(http);
        String email = body.get("email") == null ? null : body.get("email").trim();
        rateLimiter.check("password-reset-client", client, 10, java.time.Duration.ofMinutes(10), tooMany);
        if (email != null && !email.isEmpty()) {
            // 同一來源對同一信箱 30 分鐘 3 次；信箱總量較寬（1 小時 10 次），避免他人耗盡本人的申請次數
            rateLimiter.check("password-reset-email-client", email + "|" + client, 3, java.time.Duration.ofMinutes(30), tooMany);
            rateLimiter.check("password-reset-email", email, 10, java.time.Duration.ofHours(1), tooMany);
        }
        passwordResetService.requestReset(body.get("email"));
        return ApiResponse.success("若此 Email 已註冊，重設密碼連結會在幾分鐘內寄達，請於 1 小時內使用", null);
    }

    @PostMapping("/password-reset/confirm")
    @Operation(summary = "重設密碼", description = "以重設信中的 token 設定新密碼")
    public ApiResponse<Void> confirmPasswordReset(@RequestBody java.util.Map<String, String> body) {
        com.info.ecommerce.modules.auth.entity.User user =
                passwordResetService.reset(body.getOrDefault("token", ""), body.get("newPassword"));
        // 已證明擁有信箱並換了新密碼：解除帳號層級的登入暫停
        rateLimiter.reset("login-user", user.getUsername());
        return ApiResponse.success("密碼已重設，請使用新密碼登入", null);
    }

    @PostMapping("/email-verification/confirm")
    @Operation(summary = "完成 Email 驗證", description = "以驗證信中的 token 完成驗證")
    public ApiResponse<Void> confirmEmailVerification(@RequestBody java.util.Map<String, String> body) {
        emailVerificationService.confirm(body.getOrDefault("token", ""));
        return ApiResponse.success("Email 驗證完成", null);
    }
}
