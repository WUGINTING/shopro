package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.AdminNotificationDTO;
import com.info.ecommerce.modules.system.service.AdminNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "管理通知", description = "Admin notification center")
public class AdminNotificationController {

    private final AdminNotificationService notificationService;

    @GetMapping
    @Operation(summary = "取得最新的系統通知")
    public ApiResponse<List<AdminNotificationDTO>> getNotifications() {
        return ApiResponse.success(notificationService.listRecentNotifications(10, false));
    }

    @GetMapping("/unread")
    @Operation(summary = "取得仍未閱讀的通知")
    public ApiResponse<List<AdminNotificationDTO>> getUnreadNotifications() {
        return ApiResponse.success(notificationService.listRecentNotifications(10, true));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "標記通知為已讀")
    public ApiResponse<Void> markAsRead(@PathVariable @Min(1) Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success(null);
    }
}
