package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.NotificationConfigDTO;
import com.info.ecommerce.modules.system.service.NotificationConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知設定控制器
 */
@RestController
@RequestMapping("/api/system/notification-config")
@RequiredArgsConstructor
@Tag(name = "通知設定", description = "通知設定管理")
public class NotificationConfigController {

    private final NotificationConfigService notificationConfigService;

    @PostMapping
    @Operation(summary = "創建通知設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NotificationConfigDTO> createNotificationConfig(@Valid @RequestBody NotificationConfigDTO dto) {
        return ApiResponse.success("通知設定已創建", notificationConfigService.createNotificationConfig(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新通知設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NotificationConfigDTO> updateNotificationConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id,
            @Valid @RequestBody NotificationConfigDTO dto) {
        return ApiResponse.success("通知設定已更新", notificationConfigService.updateNotificationConfig(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得通知設定詳情")
    public ApiResponse<NotificationConfigDTO> getNotificationConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id) {
        return ApiResponse.success(notificationConfigService.getNotificationConfig(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除通知設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteNotificationConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id) {
        notificationConfigService.deleteNotificationConfig(id);
        return ApiResponse.success("通知設定已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢通知設定")
    public ApiResponse<Page<NotificationConfigDTO>> listNotificationConfigs(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(notificationConfigService.listNotificationConfigs(pageable));
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的通知設定")
    public ApiResponse<List<NotificationConfigDTO>> listEnabledNotificationConfigs() {
        return ApiResponse.success(notificationConfigService.listEnabledNotificationConfigs());
    }

    @GetMapping("/type/{notificationType}")
    @Operation(summary = "依通知類型查詢")
    public ApiResponse<Page<NotificationConfigDTO>> listByNotificationType(
            @Parameter(description = "通知類型") @PathVariable String notificationType,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(notificationConfigService.listByNotificationType(notificationType, pageable));
    }

    @GetMapping("/event/{eventType}")
    @Operation(summary = "依事件類型查詢")
    public ApiResponse<List<NotificationConfigDTO>> listByEventType(
            @Parameter(description = "事件類型") @PathVariable String eventType) {
        return ApiResponse.success(notificationConfigService.listByEventType(eventType));
    }
}
