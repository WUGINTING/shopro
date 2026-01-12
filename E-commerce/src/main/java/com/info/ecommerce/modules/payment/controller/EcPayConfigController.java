package com.info.ecommerce.modules.payment.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.payment.dto.EcPayConfigDTO;
import com.info.ecommerce.modules.payment.service.EcPayConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * ECPay 配置控制器
 */
@RestController
@RequestMapping("/api/payment/ecpay/config")
@RequiredArgsConstructor
@Tag(name = "ECPay 配置管理", description = "管理 ECPay 支付閘道的配置資訊")
public class EcPayConfigController {

    private final EcPayConfigService ecPayConfigService;

    @GetMapping
    @Operation(summary = "取得 ECPay 配置", description = "取得當前使用的 ECPay 配置")
    public ApiResponse<EcPayConfigDTO> getConfig() {
        return ApiResponse.success(ecPayConfigService.getConfig());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根據 ID 取得配置")
    public ApiResponse<EcPayConfigDTO> getConfigById(
            @Parameter(description = "配置 ID") @PathVariable Long id) {
        return ApiResponse.success(ecPayConfigService.getConfigById(id));
    }

    @PostMapping
    @Operation(summary = "創建 ECPay 配置", description = "創建新的 ECPay 配置")
    public ApiResponse<EcPayConfigDTO> createConfig(@Valid @RequestBody EcPayConfigDTO dto) {
        return ApiResponse.success("配置已創建", ecPayConfigService.saveConfig(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 ECPay 配置", description = "更新現有的 ECPay 配置")
    public ApiResponse<EcPayConfigDTO> updateConfig(
            @Parameter(description = "配置 ID") @PathVariable Long id,
            @Valid @RequestBody EcPayConfigDTO dto) {
        dto.setId(id);
        return ApiResponse.success("配置已更新", ecPayConfigService.saveConfig(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除 ECPay 配置", description = "刪除指定的 ECPay 配置")
    public ApiResponse<Void> deleteConfig(
            @Parameter(description = "配置 ID") @PathVariable Long id) {
        ecPayConfigService.deleteConfig(id);
        return ApiResponse.success("配置已刪除", null);
    }
}

