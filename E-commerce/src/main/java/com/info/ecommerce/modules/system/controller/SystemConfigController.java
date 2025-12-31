package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.SystemConfigDTO;
import com.info.ecommerce.modules.system.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 系統基本設定控制器
 */
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
@Tag(name = "系統設定", description = "系統基本設定管理")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    @Operation(summary = "取得系統設定")
    public ApiResponse<SystemConfigDTO> getSystemConfig() {
        return ApiResponse.success(systemConfigService.getSystemConfig());
    }

    @PutMapping
    @Operation(summary = "更新系統設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SystemConfigDTO> saveSystemConfig(@Valid @RequestBody SystemConfigDTO dto) {
        return ApiResponse.success("系統設定已更新", systemConfigService.saveSystemConfig(dto));
    }
}
