package com.info.ecommerce.modules.auth.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.auth.dto.UserDTO;
import com.info.ecommerce.modules.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for user account management
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "帳號管理", description = "使用者帳號管理功能")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "取得所有使用者", description = "取得系統中所有使用者列表（僅限管理員）")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        return ApiResponse.success("成功取得使用者列表", userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "取得使用者詳情", description = "根據 ID 取得特定使用者的詳細資訊（僅限管理員）")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        return ApiResponse.success("成功取得使用者資訊", userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "新增使用者", description = "建立新的使用者帳號（僅限管理員）")
    public ApiResponse<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ApiResponse.success("使用者建立成功", userService.createUser(userDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新使用者", description = "更新現有使用者的資訊（僅限管理員）")
    public ApiResponse<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return ApiResponse.success("使用者更新成功", userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "刪除使用者", description = "刪除指定的使用者帳號（僅限管理員）")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("使用者刪除成功", null);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "啟用/停用使用者", description = "切換使用者帳號的啟用狀態（僅限管理員）")
    public ApiResponse<UserDTO> toggleUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        return ApiResponse.success("使用者狀態已更新", userService.toggleUserStatus(id, enabled));
    }
}
