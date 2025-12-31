package com.info.ecommerce.modules.system.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.system.dto.PaymentConfigDTO;
import com.info.ecommerce.modules.system.service.PaymentConfigService;
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
 * 金流設定控制器
 */
@RestController
@RequestMapping("/api/system/payment-config")
@RequiredArgsConstructor
@Tag(name = "金流設定", description = "金流設定管理")
public class PaymentConfigController {

    private final PaymentConfigService paymentConfigService;

    @PostMapping
    @Operation(summary = "創建金流設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentConfigDTO> createPaymentConfig(@Valid @RequestBody PaymentConfigDTO dto) {
        return ApiResponse.success("金流設定已創建", paymentConfigService.createPaymentConfig(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新金流設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentConfigDTO> updatePaymentConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id,
            @Valid @RequestBody PaymentConfigDTO dto) {
        return ApiResponse.success("金流設定已更新", paymentConfigService.updatePaymentConfig(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得金流設定詳情")
    public ApiResponse<PaymentConfigDTO> getPaymentConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id) {
        return ApiResponse.success(paymentConfigService.getPaymentConfig(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除金流設定")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deletePaymentConfig(
            @Parameter(description = "設定 ID") @PathVariable Long id) {
        paymentConfigService.deletePaymentConfig(id);
        return ApiResponse.success("金流設定已刪除", null);
    }

    @GetMapping
    @Operation(summary = "分頁查詢金流設定")
    public ApiResponse<Page<PaymentConfigDTO>> listPaymentConfigs(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(paymentConfigService.listPaymentConfigs(pageable));
    }

    @GetMapping("/enabled")
    @Operation(summary = "取得已啟用的金流設定")
    public ApiResponse<List<PaymentConfigDTO>> listEnabledPaymentConfigs() {
        return ApiResponse.success(paymentConfigService.listEnabledPaymentConfigs());
    }

    @GetMapping("/method/{paymentMethod}")
    @Operation(summary = "依付款方式查詢")
    public ApiResponse<List<PaymentConfigDTO>> listByPaymentMethod(
            @Parameter(description = "付款方式") @PathVariable String paymentMethod) {
        return ApiResponse.success(paymentConfigService.listByPaymentMethod(paymentMethod));
    }
}
