package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.NotificationDTO;
import com.info.ecommerce.modules.system.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "通知", description = "系統通知管理")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * 獲取當前用戶的未讀通知
     */
    @GetMapping("/unread")
    @Operation(summary = "獲取未讀通知")
    public ApiResponse<List<NotificationDTO>> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(notificationService.getUnreadNotifications(userId));
    }

    /**
     * 獲取未讀通知數量
     */
    @GetMapping("/unread/count")
    @Operation(summary = "獲取未讀通知數量")
    public ApiResponse<Long> getUnreadCount() {
        Long userId = getCurrentUserId();
        return ApiResponse.success(notificationService.getUnreadCount(userId));
    }

    /**
     * 標記通知為已讀
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "標記通知為已讀")
    public ApiResponse<String> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return ApiResponse.success("通知已標記為已讀");
    }

    /**
     * 標記所有通知為已讀
     */
    @PutMapping("/read-all")
    @Operation(summary = "標記所有通知為已讀")
    public ApiResponse<String> markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ApiResponse.success("所有通知已標記為已讀");
    }

    /**
     * 獲取當前用戶 ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof User) {
                return ((User) principal).getId();
            } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                org.springframework.security.core.userdetails.UserDetails userDetails = 
                        (org.springframework.security.core.userdetails.UserDetails) principal;
                try {
                    User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
                    if (user != null) {
                        return user.getId();
                    }
                } catch (Exception e) {
                    // 忽略錯誤，返回 null
                }
            }
        }
        return null;
    }
}

