package com.info.ecommerce.modules.payment.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.payment.dto.PaymentCallbackLogDTO;
import com.info.ecommerce.modules.payment.service.PaymentCallbackLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付回調記錄控制器
 */
@RestController
@RequestMapping("/api/payment/callback-logs")
@RequiredArgsConstructor
@Tag(name = "支付回調記錄", description = "查詢和管理支付回調記錄，用於除錯和追蹤")
public class PaymentCallbackLogController {

    private final PaymentCallbackLogService callbackLogService;

    @GetMapping
    @Operation(summary = "取得回調記錄列表", description = "分頁查詢所有支付回調記錄")
    public ApiResponse<Page<PaymentCallbackLogDTO>> getCallbackLogs(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(callbackLogService.getCallbackLogs(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "取得回調記錄詳情", description = "根據 ID 取得回調記錄的詳細資訊")
    public ApiResponse<PaymentCallbackLogDTO> getCallbackLog(
            @Parameter(description = "記錄 ID") @PathVariable Long id) {
        return ApiResponse.success(callbackLogService.getCallbackLog(id));
    }

    @GetMapping("/order/{orderNumber}")
    @Operation(summary = "根據訂單編號查詢", description = "查詢指定訂單的所有回調記錄")
    public ApiResponse<List<PaymentCallbackLogDTO>> getCallbackLogsByOrderNumber(
            @Parameter(description = "訂單編號") @PathVariable String orderNumber) {
        return ApiResponse.success(callbackLogService.getCallbackLogsByOrderNumber(orderNumber));
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "根據交易 ID 查詢", description = "查詢指定交易的所有回調記錄")
    public ApiResponse<List<PaymentCallbackLogDTO>> getCallbackLogsByTransactionId(
            @Parameter(description = "交易 ID") @PathVariable String transactionId) {
        return ApiResponse.success(callbackLogService.getCallbackLogsByTransactionId(transactionId));
    }

    @GetMapping("/gateway/{gateway}")
    @Operation(summary = "根據支付閘道類型查詢", description = "查詢指定支付閘道的所有回調記錄")
    public ApiResponse<Page<PaymentCallbackLogDTO>> getCallbackLogsByGateway(
            @Parameter(description = "支付閘道類型") @PathVariable String gateway,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(callbackLogService.getCallbackLogsByGateway(gateway, pageable));
    }
}

