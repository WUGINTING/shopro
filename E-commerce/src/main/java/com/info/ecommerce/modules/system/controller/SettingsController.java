package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系統設置控制器
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Tag(name = "系統設置", description = "系統設置管理")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping("/system")
    @Operation(summary = "獲取系統設置")
    public ApiResponse<Map<String, Object>> getSystemSettings() {
        return ApiResponse.success(settingsService.getSystemSettings());
    }

    @PutMapping("/system")
    @Operation(summary = "更新系統設置")
    public ApiResponse<Map<String, Object>> updateSystemSettings(@RequestBody Map<String, Object> settings) {
        return ApiResponse.success(settingsService.updateSystemSettings(settings));
    }

    @GetMapping("/email")
    @Operation(summary = "獲取郵件設置")
    public ApiResponse<Map<String, Object>> getEmailSettings() {
        return ApiResponse.success(settingsService.getEmailSettings());
    }

    @PutMapping("/email")
    @Operation(summary = "更新郵件設置")
    public ApiResponse<Map<String, Object>> updateEmailSettings(@RequestBody Map<String, Object> settings) {
        return ApiResponse.success(settingsService.updateEmailSettings(settings));
    }

    @PostMapping("/email/test")
    @Operation(summary = "測試郵件設置")
    public ApiResponse<String> testEmailSettings(@RequestBody Map<String, String> request) {
        String testEmail = request.get("testEmail");
        settingsService.testEmailSettings(testEmail);
        return ApiResponse.success("測試郵件已發送");
    }

    @GetMapping("/notification")
    @Operation(summary = "獲取通知設置")
    public ApiResponse<Map<String, Object>> getNotificationSettings() {
        return ApiResponse.success(settingsService.getNotificationSettings());
    }

    @PutMapping("/notification")
    @Operation(summary = "更新通知設置")
    public ApiResponse<Map<String, Object>> updateNotificationSettings(@RequestBody Map<String, Object> settings) {
        return ApiResponse.success(settingsService.updateNotificationSettings(settings));
    }

    @GetMapping("/security")
    @Operation(summary = "獲取安全設置")
    public ApiResponse<Map<String, Object>> getSecuritySettings() {
        return ApiResponse.success(settingsService.getSecuritySettings());
    }

    @PutMapping("/security")
    @Operation(summary = "更新安全設置")
    public ApiResponse<Map<String, Object>> updateSecuritySettings(@RequestBody Map<String, Object> settings) {
        return ApiResponse.success(settingsService.updateSecuritySettings(settings));
    }

    @GetMapping("/all")
    @Operation(summary = "獲取所有設置")
    public ApiResponse<Map<String, Map<String, Object>>> getAllSettings() {
        return ApiResponse.success(settingsService.getAllSettings());
    }

    @PostMapping("/reset-defaults")
    @Operation(summary = "重置為默認設置")
    public ApiResponse<String> resetToDefaults() {
        settingsService.resetToDefaults();
        return ApiResponse.success("已重置為默認設置");
    }
}

