package com.info.ecommerce.modules.payment.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.payment.dto.PaymentStatisticsDTO;
import com.info.ecommerce.modules.payment.dto.PaymentTransactionDTO;
import com.info.ecommerce.modules.payment.entity.PaymentSetting;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import com.info.ecommerce.modules.payment.service.PaymentManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付管理控制器 - 用於後台管理
 */
@RestController
@RequestMapping("/api/payment-management")
@RequiredArgsConstructor
@Tag(name = "支付管理", description = "支付統計、交易查詢、閘道設定等後台管理功能")
@Slf4j
public class PaymentManagementController {

    private final PaymentManagementService paymentManagementService;

    @GetMapping("/statistics")
    @Operation(summary = "取得支付統計資料", description = "取得今日、本月的交易統計及各閘道佔比")
    public ApiResponse<PaymentStatisticsDTO> getStatistics() {
        log.info("Fetching payment statistics");
        try {
            PaymentStatisticsDTO statistics = paymentManagementService.getStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("Failed to fetch statistics", e);
            return ApiResponse.error("取得統計資料失敗: " + e.getMessage());
        }
    }

    @GetMapping("/transactions")
    @Operation(summary = "查詢交易記錄", description = "支援多條件查詢交易記錄")
    public ApiResponse<List<PaymentTransactionDTO>> searchTransactions(
            @Parameter(description = "關鍵字 (訂單編號或交易 ID)") @RequestParam(required = false) String keyword,
            @Parameter(description = "支付閘道") @RequestParam(required = false) PaymentGateway gateway,
            @Parameter(description = "交易狀態") @RequestParam(required = false) PaymentGatewayStatus status,
            @Parameter(description = "開始時間") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "結束時間") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Searching transactions: keyword={}, gateway={}, status={}", keyword, gateway, status);
        try {
            List<PaymentTransactionDTO> transactions = paymentManagementService.searchTransactions(
                keyword, gateway, status, startDate, endDate);
            return ApiResponse.success(transactions);
        } catch (Exception e) {
            log.error("Failed to search transactions", e);
            return ApiResponse.error("查詢交易記錄失敗: " + e.getMessage());
        }
    }

    @GetMapping("/transactions/{id}")
    @Operation(summary = "取得交易詳情", description = "根據 ID 取得單筆交易的詳細資訊")
    public ApiResponse<PaymentTransactionDTO> getTransaction(
            @Parameter(description = "交易 ID") @PathVariable Long id) {
        
        log.info("Fetching transaction detail: id={}", id);
        try {
            PaymentTransactionDTO transaction = paymentManagementService.getTransaction(id);
            return ApiResponse.success(transaction);
        } catch (Exception e) {
            log.error("Failed to fetch transaction detail", e);
            return ApiResponse.error("取得交易詳情失敗: " + e.getMessage());
        }
    }

    @GetMapping("/settings")
    @Operation(summary = "取得所有支付設定", description = "取得所有支付閘道的設定資訊")
    public ApiResponse<List<PaymentSetting>> getAllSettings() {
        log.info("Fetching all payment settings");
        try {
            List<PaymentSetting> settings = paymentManagementService.getAllSettings();
            return ApiResponse.success(settings);
        } catch (Exception e) {
            log.error("Failed to fetch settings", e);
            return ApiResponse.error("取得支付設定失敗: " + e.getMessage());
        }
    }

    @GetMapping("/settings/{gateway}")
    @Operation(summary = "取得單一支付設定", description = "根據閘道類型取得設定")
    public ApiResponse<PaymentSetting> getSetting(
            @Parameter(description = "支付閘道類型") @PathVariable PaymentGateway gateway) {
        
        log.info("Fetching payment setting for gateway: {}", gateway);
        try {
            PaymentSetting setting = paymentManagementService.getSetting(gateway);
            if (setting == null) {
                return ApiResponse.error("找不到該支付閘道的設定");
            }
            return ApiResponse.success(setting);
        } catch (Exception e) {
            log.error("Failed to fetch setting", e);
            return ApiResponse.error("取得支付設定失敗: " + e.getMessage());
        }
    }

    @PutMapping("/settings")
    @Operation(summary = "更新支付設定", description = "更新支付閘道的設定資訊")
    public ApiResponse<PaymentSetting> updateSetting(
            @RequestBody PaymentSetting setting) {
        
        log.info("Updating payment setting for gateway: {}", setting.getGateway());
        try {
            PaymentSetting updated = paymentManagementService.updateSetting(setting);
            return ApiResponse.success("支付設定已更新", updated);
        } catch (Exception e) {
            log.error("Failed to update setting", e);
            return ApiResponse.error("更新支付設定失敗: " + e.getMessage());
        }
    }

    @GetMapping("/settings/{gateway}/availability")
    @Operation(summary = "檢查閘道可用性", description = "檢查支付閘道是否可用")
    public ApiResponse<Boolean> checkAvailability(
            @Parameter(description = "支付閘道類型") @PathVariable PaymentGateway gateway) {
        
        log.info("Checking gateway availability: {}", gateway);
        try {
            boolean available = paymentManagementService.isGatewayAvailable(gateway);
            return ApiResponse.success(available);
        } catch (Exception e) {
            log.error("Failed to check availability", e);
            return ApiResponse.error("檢查閘道可用性失敗: " + e.getMessage());
        }
    }
}
