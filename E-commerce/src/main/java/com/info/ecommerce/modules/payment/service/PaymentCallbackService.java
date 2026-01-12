package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderPayment;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PaymentStatus;
import com.info.ecommerce.modules.order.repository.OrderPaymentRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.order.service.OrderHistoryService;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.entity.PaymentGatewayTransaction;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import com.info.ecommerce.modules.payment.repository.PaymentGatewayTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 支付回調處理服務
 * 統一處理各支付閘道的回調通知
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCallbackService {

    private final OrderRepository orderRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderHistoryService orderHistoryService;
    private final PaymentGatewayTransactionRepository transactionRepository;

    /**
     * 處理支付成功回調
     * 
     * @param response 支付回應資料
     * @return 是否處理成功
     */
    @Transactional
    public boolean handlePaymentSuccess(PaymentResponseDTO response) {
        try {
            String orderNumber = response.getOrderNumber();
            log.info("Handling payment success for order: {}, transaction: {}", orderNumber, response.getTransactionId());
            
            // 查找訂單（先嘗試精確匹配，如果失敗則嘗試前綴匹配）
            Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
            
            // 如果精確匹配失敗，可能是因為訂單編號被截取了，嘗試使用前綴匹配
            if (orderOpt.isEmpty() && orderNumber != null && orderNumber.length() >= 10) {
                log.warn("Exact match failed for order number: {}, trying prefix match...", orderNumber);
                // 嘗試匹配以該訂單編號開頭的訂單（用於處理被截取的訂單編號）
                List<Order> ordersByPrefix = orderRepository.findAll().stream()
                        .filter(order -> order.getOrderNumber() != null && order.getOrderNumber().startsWith(orderNumber))
                        .limit(1)
                        .toList();
                if (!ordersByPrefix.isEmpty()) {
                    orderOpt = Optional.of(ordersByPrefix.get(0));
                    log.info("Found order by prefix match: {} -> {}", orderNumber, orderOpt.get().getOrderNumber());
                }
            }
            
            if (orderOpt.isEmpty()) {
                log.error("Order not found for payment success: {}", orderNumber);
                // 記錄一些訂單編號示例用於調試（但不記錄太多以避免性能問題）
                try {
                    List<String> sampleOrderNumbers = orderRepository.findAll().stream()
                            .limit(5)
                            .map(Order::getOrderNumber)
                            .toList();
                    log.error("Sample order numbers in database: {}", sampleOrderNumbers);
                } catch (Exception e) {
                    log.error("Failed to fetch sample order numbers", e);
                }
                return false;
            }
            
            Order order = orderOpt.get();
            log.info("Order found: {}, current status: {}", orderNumber, order.getStatus());
            
            // 檢查訂單狀態（只允許待付款或已付款狀態的訂單進行支付成功處理）
            // 注意：數據庫約束允許：PENDING_PAYMENT, PAID, PROCESSING, COMPLETED, CANCELLED, REFUNDED
            // 支付成功後更新為 PAID（已付款）狀態
            if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAID) {
                log.warn("Order {} is not in PENDING_PAYMENT or PROCESSING status, current status: {}", 
                        orderNumber, order.getStatus());
                // 如果已經是完成、取消或退款狀態，不需要處理
                if (order.getStatus() == OrderStatus.COMPLETED || 
                    order.getStatus() == OrderStatus.CANCELLED || 
                    order.getStatus() == OrderStatus.REFUNDED ||
                    order.getStatus() == OrderStatus.PROCESSING) {
                    log.info("Order {} is already in status: {}, skipping payment update", 
                            orderNumber, order.getStatus());
                    return true;
                }
                return false;
            }
            
            // 更新訂單狀態
            // 數據庫 CHECK 約束 CK__orders__status__6A30C649 允許以下狀態：
            // PENDING_PAYMENT, PAID, PROCESSING, COMPLETED, CANCELLED, REFUNDED
            // 支付成功後更新為 PAID（已付款）狀態
            OrderStatus oldStatus = order.getStatus();
            order.setStatus(OrderStatus.PAID);
            order.setPaymentTime(LocalDateTime.now());
            orderRepository.save(order);
            
            log.info("Order status updated from {} to PAID for order: {} (Payment successful)", oldStatus, orderNumber);
            
            // 建立或更新付款記錄
            OrderPayment payment = createOrUpdatePayment(order, response);
            
            // 建立或更新支付閘道交易記錄（用於統計）
            createOrUpdateTransaction(order, response, PaymentGatewayStatus.SUCCESS);
            
            // 記錄歷史
            orderHistoryService.recordHistory(
                    order.getId(),
                    "PAYMENT_SUCCESS",
                    String.format("支付成功 - 閘道: %s, 交易ID: %s", 
                            response.getGateway().getDisplayName(),
                            response.getTransactionId()),
                    oldStatus.name(),
                    OrderStatus.PAID.name(),
                    null,
                    null
            );
            
            log.info("Payment successfully processed for order: {}, transaction: {}", 
                    orderNumber, response.getTransactionId());
            
            return true;
            
        } catch (Exception e) {
            log.error("Failed to handle payment success", e);
            return false;
        }
    }

    /**
     * 處理支付失敗回調
     * 
     * @param response 支付回應資料
     * @return 是否處理成功
     */
    @Transactional
    public boolean handlePaymentFailure(PaymentResponseDTO response) {
        try {
            String orderNumber = response.getOrderNumber();
            
            // 查找訂單
            Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
            if (orderOpt.isEmpty()) {
                log.error("Order not found for payment failure: {}", orderNumber);
                return false;
            }
            
            Order order = orderOpt.get();
            
            // 建立失敗的付款記錄
            OrderPayment payment = OrderPayment.builder()
                    .orderId(order.getId())
                    .paymentStatus(PaymentStatus.FAILED)
                    .paymentMethod(response.getGateway().getDisplayName())
                    .paymentAmount(response.getAmount())
                    .paymentGateway(response.getGateway().name())
                    .gatewayTransactionId(response.getTransactionId())
                    .notes("支付失敗: " + response.getErrorMessage())
                    .build();
            orderPaymentRepository.save(payment);
            
            // 建立支付閘道交易記錄（用於統計）
            createOrUpdateTransaction(order, response, PaymentGatewayStatus.FAILED);
            
            // 記錄歷史
            orderHistoryService.recordHistory(
                    order.getId(),
                    "PAYMENT_FAILED",
                    String.format("支付失敗 - 閘道: %s, 原因: %s", 
                            response.getGateway().getDisplayName(),
                            response.getErrorMessage()),
                    order.getStatus().name(),
                    order.getStatus().name(),
                    null,
                    null
            );
            
            log.info("Payment failure recorded for order: {}, error: {}", 
                    orderNumber, response.getErrorMessage());
            
            return true;
            
        } catch (Exception e) {
            log.error("Failed to handle payment failure", e);
            return false;
        }
    }

    /**
     * 處理支付取消回調
     * 
     * @param orderNumber 訂單編號
     * @return 是否處理成功
     */
    @Transactional
    public boolean handlePaymentCancellation(String orderNumber) {
        try {
            // 查找訂單
            Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
            if (orderOpt.isEmpty()) {
                log.error("Order not found for payment cancellation: {}", orderNumber);
                return false;
            }
            
            Order order = orderOpt.get();
            
            // 只有待付款或處理中的訂單可以取消
            if (order.getStatus() != OrderStatus.PENDING_PAYMENT 
                    && order.getStatus() != OrderStatus.PROCESSING) {
                log.warn("Order {} cannot be cancelled, current status: {}", 
                        orderNumber, order.getStatus());
                return false;
            }
            
            // 更新訂單狀態
            OrderStatus oldStatus = order.getStatus();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            
            // 記錄歷史
            orderHistoryService.recordHistory(
                    order.getId(),
                    "PAYMENT_CANCELLED",
                    "支付已取消",
                    oldStatus.name(),
                    OrderStatus.CANCELLED.name(),
                    null,
                    null
            );
            
            log.info("Payment cancellation processed for order: {}", orderNumber);
            
            return true;
            
        } catch (Exception e) {
            log.error("Failed to handle payment cancellation", e);
            return false;
        }
    }

    /**
     * 建立或更新付款記錄
     */
    private OrderPayment createOrUpdatePayment(Order order, PaymentResponseDTO response) {
        // 查找是否已存在該交易的付款記錄
        OrderPayment payment = orderPaymentRepository
                .findByOrderIdAndGatewayTransactionId(order.getId(), response.getTransactionId())
                .orElse(OrderPayment.builder()
                        .orderId(order.getId())
                        .build());
        
        // 更新付款記錄
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentMethod(response.getGateway().getDisplayName());
        payment.setPaymentAmount(response.getAmount());
        payment.setPaymentGateway(response.getGateway().name());
        payment.setGatewayTransactionId(response.getTransactionId());
        payment.setTransactionId(response.getTransactionId());
        payment.setPaymentTime(LocalDateTime.now());
        
        return orderPaymentRepository.save(payment);
    }

    /**
     * 建立或更新支付閘道交易記錄（用於統計）
     */
    private PaymentGatewayTransaction createOrUpdateTransaction(Order order, PaymentResponseDTO response, PaymentGatewayStatus status) {
        // 查找是否已存在該交易的記錄
        Optional<PaymentGatewayTransaction> existingTransaction = transactionRepository
                .findByTransactionId(response.getTransactionId());
        
        PaymentGatewayTransaction transaction;
        if (existingTransaction.isPresent()) {
            transaction = existingTransaction.get();
            transaction.setStatus(status);
            transaction.setUpdatedAt(LocalDateTime.now());
            if (response.getErrorMessage() != null) {
                transaction.setErrorMessage(response.getErrorMessage());
            }
        } else {
            // 建立新記錄
            transaction = PaymentGatewayTransaction.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .gateway(response.getGateway())
                    .transactionId(response.getTransactionId())
                    .status(status)
                    .amount(response.getAmount())
                    .currency("TWD") // 預設為台幣
                    .paymentUrl(response.getPaymentUrl())
                    .errorMessage(response.getErrorMessage())
                    .rawResponse(response.getRawResponse())
                    .build();
        }
        
        return transactionRepository.save(transaction);
    }
}
