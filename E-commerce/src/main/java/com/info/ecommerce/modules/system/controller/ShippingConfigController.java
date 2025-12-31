package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.ShippingConfigDTO;
import com.info.ecommerce.modules.system.service.ShippingConfigService;
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
 * 物流設定控制器
 */
@RestController
@RequestMapping("/api/system/shipping-config")
@RequiredArgsConstructor
@Tag(name = "物流設定", description = "物流設定管理")
public class ShippingConfigController {

    private final ShippingConfigService shippingConfigService;

    @PostMapping
    @Operation(summary = "創建物流設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShippingConfigDTO> createShippingConfig(@Valid @RequestBody ShippingConfigDTO dto) {
        return ApiResponse.success("物流設定已創建", shippingConfigService.createShippingConfig(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新物流設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShippingConfigDTO> updateShippingConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id,
            @Valid @RequestBody ShippingConfigDTO dto) {
        return ApiResponse.success("物流設定已更新", shippingConfigService.updateShippingConfig(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得物流設定詳情")
    public ApiResponse<ShippingConfigDTO> getShippingConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id) {
        return ApiResponse.success(shippingConfigService.getShippingConfig(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除物流設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteShippingConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id) {
        shippingConfigService.deleteShippingConfig(id);
        return ApiResponse.success("物流設定已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢物流設定")
    public ApiResponse<Page<ShippingConfigDTO>> listShippingConfigs(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(shippingConfigService.listShippingConfigs(pageable));
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的物流設定")
    public ApiResponse<List<ShippingConfigDTO>> listEnabledShippingConfigs() {
        return ApiResponse.success(shippingConfigService.listEnabledShippingConfigs());
    }

    @GetMapping("/method/{shippingMethod}")
    @Operation(summary = "依配送方式查詢")
    public ApiResponse<List<ShippingConfigDTO>> listByShippingMethod(
            @Parameter(description = "配送方式") @PathVariable String shippingMethod) {
        return ApiResponse.success(shippingConfigService.listByShippingMethod(shippingMethod));
    }
}
