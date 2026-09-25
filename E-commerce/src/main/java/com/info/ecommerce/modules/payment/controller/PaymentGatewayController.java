package com.info.ecommerce.modules.payment.controller;

import com.info.ecommerce.common.ApiResponse;
import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.auth.service.CurrentUserService;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderRepository;
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
    private final com.info.ecommerce.modules.payment.service.PaymentCallbackLogService paymentCallbackLogService;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;
    private final com.info.ecommerce.modules.payment.repository.PaymentGatewayTransactionRepository transactionRepository;
    private final com.info.ecommerce.modules.payment.repository.PaymentSettingRepository paymentSettingRepository;

    /**
     * 找出要付款的訂單並檢查權限與狀態；付款金額一律以訂單金額為準，不採用前端傳入的金額
     */
    private Order resolvePayableOrder(Long orderId, String orderNumber) {
        Order order = (orderId != null
                ? orderRepository.findById(orderId)
                : java.util.Optional.ofNullable(orderNumber).flatMap(orderRepository::findByOrderNumber))
                .orElseThrow(() -> new BusinessException("訂單不存在"));
        currentUserService.assertCanAccessOrder(order.getCustomerId(), order.getCustomerEmail());
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("訂單狀態為「" + order.getStatus().getDescription() + "」，無法付款");
        }
        return order;
    }

    @PostMapping("/create")
    @Operation(summary = "創建支付請求", description = "透過指定的支付閘道創建支付請求，返回支付 URL")
    public ApiResponse<PaymentResponseDTO> createPayment(
            @Parameter(description = "支付閘道類型") @RequestParam PaymentGateway gateway,
            @Valid @RequestBody PaymentRequestDTO request) {

        Order order = resolvePayableOrder(request.getOrderId(), request.getOrderNumber());
        request.setOrderId(order.getId());
        request.setOrderNumber(order.getOrderNumber());
        request.setAmount(order.getTotalAmount());
        // 送到金流的資料一律取自訂單，不採用前端傳入的值（避免插入額外的簽章參數或導向外部網址）
        request.setCurrency("TWD");
        request.setProductName("訂單 " + order.getOrderNumber());
        request.setCustomerName(order.getCustomerName());
        request.setCustomerEmail(order.getCustomerEmail());
        request.setCustomerPhone(order.getCustomerPhone());
        request.setClientBackUrl(null);

        log.info("Creating payment with gateway: {} for order: {}", gateway, request.getOrderNumber());
        
        try {
            PaymentGatewayService service = paymentGatewayFactory.getPaymentGatewayService(gateway);
            PaymentResponseDTO response = service.createPayment(request);
            
            if (response.getStatus() == com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus.FAILED) {
                return new ApiResponse<>(false, response.getErrorMessage(), response);
            }

            // LINE PAY：記錄交易編號，確認回呼時只接受由此建立的交易
            if (gateway == PaymentGateway.LINE_PAY && response.getTransactionId() != null) {
                transactionRepository.save(com.info.ecommerce.modules.payment.entity.PaymentGatewayTransaction.builder()
                        .orderId(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .gateway(PaymentGateway.LINE_PAY)
                        .transactionId(response.getTransactionId())
                        .status(com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus.INITIATED)
                        .amount(order.getTotalAmount())
                        .currency("TWD")
                        .paymentUrl(response.getPaymentUrl())
                        .build());
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

        Order order = resolvePayableOrder(null, confirm.getOrderNumber());
        confirm.setAmount(order.getTotalAmount());
        
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
        
        long startTime = System.currentTimeMillis();
        String status = "ERROR";
        String processResult = "";
        String errorMessage = null;
        PaymentResponseDTO response = null;
        
        log.info("=== ECPay Callback Received ===");
        log.info("Received ECPay callback with params: {}", params);
        
        try {
            // 解析回調參數
            response = ecPayService.parseCallback(params);
            log.info("Parsed callback response - Status: {}, OrderNumber: {}, TransactionId: {}, ErrorMessage: {}", 
                    response.getStatus(), response.getOrderNumber(), response.getTransactionId(), response.getErrorMessage());
            
            // 處理支付回調
            boolean success = false;
            if (response.getStatus() == com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus.SUCCESS) {
                log.info("Payment status is SUCCESS, calling handlePaymentSuccess...");
                success = paymentCallbackService.handlePaymentSuccess(response);
                log.info("handlePaymentSuccess returned: {}", success);
                
                if (success) {
                    status = "SUCCESS";
                    processResult = "支付成功，訂單狀態已更新";
                    log.info("ECPay payment successful for order: {}", response.getOrderNumber());
                    
                    // 記錄回調
                    long processTime = System.currentTimeMillis() - startTime;
                    paymentCallbackLogService.logCallback(
                            "ECPAY",
                            params,
                            response,
                            status,
                            processResult,
                            null,
                            processTime
                    );
                    
                    return ResponseEntity.ok("1|OK");
                } else {
                    status = "FAILED";
                    processResult = "支付成功但處理失敗";
                    errorMessage = "處理失敗";
                    log.error("Failed to process successful payment for order: {}", response.getOrderNumber());
                    
                    // 記錄回調
                    long processTime = System.currentTimeMillis() - startTime;
                    paymentCallbackLogService.logCallback(
                            "ECPAY",
                            params,
                            response,
                            status,
                            processResult,
                            errorMessage,
                            processTime
                    );
                    
                    return ResponseEntity.ok("0|處理失敗");
                }
            } else {
                status = "FAILED";
                processResult = "支付失敗";
                errorMessage = response.getErrorMessage();
                log.warn("Payment status is NOT SUCCESS: {}, ErrorMessage: {}", response.getStatus(), response.getErrorMessage());
                success = paymentCallbackService.handlePaymentFailure(response);
                log.error("ECPay payment failed: {}", response.getErrorMessage());
                
                // 記錄回調
                long processTime = System.currentTimeMillis() - startTime;
                paymentCallbackLogService.logCallback(
                        "ECPAY",
                        params,
                        response,
                        status,
                        processResult,
                        errorMessage,
                        processTime
                );
                
                return ResponseEntity.ok("0|" + response.getErrorMessage());
            }
        } catch (Exception e) {
            status = "ERROR";
            errorMessage = e.getMessage();
            processResult = "處理異常";
            log.error("Exception occurred while processing ECPay callback", e);
            
            // 記錄回調（即使發生異常也要記錄）
            try {
                long processTime = System.currentTimeMillis() - startTime;
                paymentCallbackLogService.logCallback(
                        "ECPAY",
                        params,
                        response,
                        status,
                        processResult,
                        errorMessage,
                        processTime
                );
            } catch (Exception logException) {
                log.error("Failed to log callback after exception", logException);
            }
            
            return ResponseEntity.ok("0|處理失敗");
        }
    }

    @GetMapping("/callback/linepay/confirm")
    @Operation(summary = "LINE PAY 確認回調", description = "LINE PAY 支付完成後的確認頁面")
    public ApiResponse<PaymentResponseDTO> linepayConfirmCallback(
            @RequestParam String transactionId,
            @RequestParam String orderId) {
        
        log.info("Received LINE PAY confirm callback - transactionId: {}, orderId: {}", transactionId, orderId);

        // 此網址任何人都能呼叫：只在 LINE PAY 已啟用，且交易編號與建立付款時記錄的同一筆訂單相符時才向 LINE PAY 確認
        boolean linePayEnabled = paymentSettingRepository.findByGateway(PaymentGateway.LINE_PAY)
                .map(setting -> Boolean.TRUE.equals(setting.getEnabled()) && !Boolean.TRUE.equals(setting.getMaintenanceMode()))
                .orElse(false);
        boolean knownTransaction = transactionRepository.findByTransactionId(transactionId)
                .filter(tx -> tx.getGateway() == PaymentGateway.LINE_PAY && orderId.equals(tx.getOrderNumber()))
                .isPresent();
        if (!linePayEnabled || !knownTransaction) {
            log.warn("Rejected LINE PAY confirm callback for unknown transaction {} / order {}", transactionId, orderId);
            return ApiResponse.error("付款資料不符，請重新付款或聯繫客服");
        }

        try {
            PaymentConfirmDTO confirm = PaymentConfirmDTO.builder()
                    .transactionId(transactionId)
                    .orderNumber(orderId)
                    .amount(orderRepository.findByOrderNumber(orderId).map(Order::getTotalAmount).orElse(null))
                    .build();
            
            PaymentGatewayService service = paymentGatewayFactory.getPaymentGatewayService(PaymentGateway.LINE_PAY);
            PaymentResponseDTO response = service.confirmPayment(confirm);
            
            // 處理支付確認結果（只有 LINE PAY 確認成功才變更訂單；失敗不寫入付款失敗紀錄，顧客可重新付款）
            if (response.getStatus() == com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus.SUCCESS) {
                paymentCallbackService.handlePaymentSuccess(response);
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

        // 此網址為瀏覽器導回、未經簽章，任何人都能呼叫，因此不變更訂單狀態：
        // 訂單維持「待付款」，顧客可重新付款，逾期未付由後台取消
        return ApiResponse.success("已取消付款，訂單仍保留為待付款", orderId);
    }
}
