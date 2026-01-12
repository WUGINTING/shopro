package com.info.ecommerce.modules.payment.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.modules.payment.dto.PaymentConfirmDTO;
import com.info.ecommerce.modules.payment.dto.PaymentRequestDTO;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.service.EcPayService;
import com.info.ecommerce.modules.payment.service.PaymentCallbackService;
import com.info.ecommerce.modules.payment.service.PaymentGatewayFactory;
import com.info.ecommerce.modules.payment.service.PaymentGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付閘道控制器
 */
@RestController
@RequestMapping("/api/payment-gateway")
@RequiredArgsConstructor
@Tag(name = "支付閘道", description = "LINE PAY、綠界等台灣支付閘道整合")
@Slf4j
public class PaymentGatewayController {

    private final PaymentGatewayFactory paymentGatewayFactory;
    private final EcPayService ecPayService;
    private final PaymentCallbackService paymentCallbackService;

    @PostMapping("/create")
    @Operation(summary = "創建支付請求", description = "透過指定的支付閘道創建支付請求，返回支付 URL")
    public ApiResponse<PaymentResponseDTO> createPayment(
            @Parameter(description = "支付閘道類型") @RequestParam PaymentGateway gateway,
            @Valid @RequestBody PaymentRequestDTO request) {
        
        log.info("Creating payment with gateway: {} for order: {}", gateway, request.getOrderNumber());
        
        try {
            PaymentGatewayService service = paymentGatewayFactory.getPaymentGatewayService(gateway);
            PaymentResponseDTO response = service.createPayment(request);
            
            if (response.getStatus() == com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus.FAILED) {
                return new ApiResponse<>(false, response.getErrorMessage(), response);
            }
            
            return ApiResponse.success("支付請求已建立", response);
        } catch (Exception e) {
            log.error("Failed to create payment", e);
            return ApiResponse.error("建立支付請求失敗: " + e.getMessage());
        }
    }

    @PostMapping("/confirm")
    @Operation(summary = "確認支付", description = "確認支付完成（主要用於 LINE PAY）")
    public ApiResponse<PaymentResponseDTO> confirmPayment(
            @Parameter(description = "支付閘道類型") @RequestParam PaymentGateway gateway,
            @Valid @RequestBody PaymentConfirmDTO confirm) {
        
        log.info("Confirming payment with gateway: {} for transaction: {}", gateway, confirm.getTransactionId());
        
        try {
            PaymentGatewayService service = paymentGatewayFactory.getPaymentGatewayService(gateway);
            PaymentResponseDTO response = service.confirmPayment(confirm);
            
            if (response.getStatus() == com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus.FAILED) {
                return new ApiResponse<>(false, response.getErrorMessage(), response);
            }
            
            return ApiResponse.success("支付確認成功", response);
        } catch (Exception e) {
            log.error("Failed to confirm payment", e);
            return ApiResponse.error("確認支付失敗: " + e.getMessage());
        }
    }

    @GetMapping("/query/{gateway}/{transactionId}")
    @Operation(summary = "查詢支付狀態", description = "查詢支付交易的目前狀態")
    public ApiResponse<PaymentResponseDTO> queryPayment(
            @Parameter(description = "支付閘道類型") @PathVariable PaymentGateway gateway,
            @Parameter(description = "交易 ID") @PathVariable String transactionId) {
        
        log.info("Querying payment with gateway: {} for transaction: {}", gateway, transactionId);
        
        try {
            PaymentGatewayService service = paymentGatewayFactory.getPaymentGatewayService(gateway);
            PaymentResponseDTO response = service.queryPayment(transactionId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Failed to query payment", e);
            return ApiResponse.error("查詢支付狀態失敗: " + e.getMessage());
        }
    }

    @PostMapping("/cancel/{gateway}/{transactionId}")
    @Operation(summary = "取消支付", description = "取消支付交易")
    public ApiResponse<PaymentResponseDTO> cancelPayment(
            @Parameter(description = "支付閘道類型") @PathVariable PaymentGateway gateway,
            @Parameter(description = "交易 ID") @PathVariable String transactionId) {
        
        log.info("Cancelling payment with gateway: {} for transaction: {}", gateway, transactionId);
        
        try {
            PaymentGatewayService service = paymentGatewayFactory.getPaymentGatewayService(gateway);
            PaymentResponseDTO response = service.cancelPayment(transactionId);
            return ApiResponse.success("支付已取消", response);
        } catch (Exception e) {
            log.error("Failed to cancel payment", e);
            return ApiResponse.error("取消支付失敗: " + e.getMessage());
        }
    }

    @PostMapping("/callback/ecpay")
    @Operation(summary = "ECPay 支付回調", description = "接收 ECPay 的支付結果通知")
    public ResponseEntity<String> ecpayCallback(@RequestParam Map<String, String> params) {
        
        log.info("=== ECPay Callback Received ===");
        log.info("Received ECPay callback with params: {}", params);
        
        try {
            PaymentResponseDTO response = ecPayService.parseCallback(params);
            log.info("Parsed callback response - Status: {}, OrderNumber: {}, TransactionId: {}, ErrorMessage: {}", 
                    response.getStatus(), response.getOrderNumber(), response.getTransactionId(), response.getErrorMessage());
            
            // 處理支付回調
            boolean success = false;
            if (response.getStatus() == com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus.SUCCESS) {
                log.info("Payment status is SUCCESS, calling handlePaymentSuccess...");
                success = paymentCallbackService.handlePaymentSuccess(response);
                log.info("handlePaymentSuccess returned: {}", success);
                if (success) {
                    log.info("ECPay payment successful for order: {}", response.getOrderNumber());
                    return ResponseEntity.ok("1|OK");
                } else {
                    log.error("Failed to process successful payment for order: {}", response.getOrderNumber());
                    return ResponseEntity.ok("0|處理失敗");
                }
            } else {
                log.warn("Payment status is NOT SUCCESS: {}, ErrorMessage: {}", response.getStatus(), response.getErrorMessage());
                success = paymentCallbackService.handlePaymentFailure(response);
                log.error("ECPay payment failed: {}", response.getErrorMessage());
                return ResponseEntity.ok("0|" + response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Exception occurred while processing ECPay callback", e);
            return ResponseEntity.ok("0|處理失敗");
        }
    }

    @GetMapping("/callback/linepay/confirm")
    @Operation(summary = "LINE PAY 確認回調", description = "LINE PAY 支付完成後的確認頁面")
    public ApiResponse<PaymentResponseDTO> linepayConfirmCallback(
            @RequestParam String transactionId,
            @RequestParam String orderId) {
        
        log.info("Received LINE PAY confirm callback - transactionId: {}, orderId: {}", transactionId, orderId);
        
        try {
            PaymentConfirmDTO confirm = PaymentConfirmDTO.builder()
                    .transactionId(transactionId)
                    .orderNumber(orderId)
                    .build();
            
            PaymentGatewayService service = paymentGatewayFactory.getPaymentGatewayService(PaymentGateway.LINE_PAY);
            PaymentResponseDTO response = service.confirmPayment(confirm);
            
            // 處理支付確認結果
            if (response.getStatus() == com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus.SUCCESS) {
                paymentCallbackService.handlePaymentSuccess(response);
            } else {
                paymentCallbackService.handlePaymentFailure(response);
            }
            
            return ApiResponse.success("支付確認成功", response);
        } catch (Exception e) {
            log.error("Failed to process LINE PAY confirm callback", e);
            return ApiResponse.error("處理 LINE PAY 確認失敗: " + e.getMessage());
        }
    }

    @GetMapping("/callback/linepay/cancel")
    @Operation(summary = "LINE PAY 取消回調", description = "LINE PAY 支付取消後的回調")
    public ApiResponse<String> linepayCancelCallback(@RequestParam String orderId) {
        
        log.info("Received LINE PAY cancel callback for orderId: {}", orderId);
        
        // 處理支付取消
        paymentCallbackService.handlePaymentCancellation(orderId);
        
        return ApiResponse.success("支付已取消", orderId);
    }
}
