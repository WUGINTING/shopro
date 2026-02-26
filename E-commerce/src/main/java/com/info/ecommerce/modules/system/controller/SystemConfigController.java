package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.StoreContentConfigDTO;
import com.info.ecommerce.modules.system.dto.SystemConfigDTO;
import com.info.ecommerce.modules.system.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
@Tag(name = "系統設定", description = "系統設定管理")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    @Operation(summary = "取得系統設定")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SystemConfigDTO> getSystemConfig() {
        return ApiResponse.success(systemConfigService.getSystemConfig());
    }

    @PutMapping
    @Operation(summary = "更新系統設定")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SystemConfigDTO> saveSystemConfig(@Valid @RequestBody SystemConfigDTO dto) {
        return ApiResponse.success("系統設定已更新", systemConfigService.saveSystemConfig(dto));
    }

    @GetMapping("/store-content")
    @Operation(summary = "取得品牌故事與聯絡我們頁面內容設定（前台可用）")
    public ApiResponse<StoreContentConfigDTO> getStoreContentConfig() {
        return ApiResponse.success(systemConfigService.getStoreContentConfig());
    }

    @PutMapping("/store-content")
    @Operation(summary = "更新品牌故事與聯絡我們頁面內容設定")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoreContentConfigDTO> saveStoreContentConfig(@RequestBody StoreContentConfigDTO dto) {
        return ApiResponse.success("品牌故事與聯絡資訊已更新", systemConfigService.saveStoreContentConfig(dto));
    }
}
