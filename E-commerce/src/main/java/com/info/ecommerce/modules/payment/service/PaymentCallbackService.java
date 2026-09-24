package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderPayment;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PaymentStatus;
import com.info.ecommerce.modules.order.repository.OrderPaymentRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.crm.service.MemberService;
import com.info.ecommerce.modules.order.service.OrderHistoryService;
import com.info.ecommerce.modules.order.service.OrderStockService;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.entity.PaymentGatewayTransaction;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import com.info.ecommerce.modules.payment.repository.PaymentGatewayTransactionRepository;
import com.info.ecommerce.modules.system.enums.AdminNotificationType;
import com.info.ecommerce.modules.system.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final MemberService memberService;
    private final AdminNotificationService adminNotificationService;
    private final OrderStockService orderStockService;

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
            
            Optional<Order> orderOpt = findOrderForCallback(orderNumber);
            if (orderOpt.isEmpty()) {
                log.error("Order not found for payment success: {}", orderNumber);
                return false;
            }

            Order order = orderOpt.get();
            log.info("Order found: {}, current status: {}", orderNumber, order.getStatus());

            // 重複通知（綠界會重送直到收到 1|OK）：已付款則直接確認，不重複寫入
            if (order.getStatus() == OrderStatus.PAID) {
                log.info("Order {} is already PAID, acknowledging duplicate callback", order.getOrderNumber());
                return true;
            }

            // 核對付款金額，避免金額不符的交易被當成已付款
            if (!amountMatches(order, response.getAmount())) {
                log.error("Payment amount mismatch for order {}: paid {}, expected {}",
                        order.getOrderNumber(), response.getAmount(), order.getTotalAmount());
                orderHistoryService.recordHistory(order.getId(), "PAYMENT_AMOUNT_MISMATCH",
                        String.format("付款金額不符 - 實付 %s，應付 %s，交易ID: %s",
                                response.getAmount(), order.getTotalAmount(), response.getTransactionId()),
                        order.getStatus().name(), order.getStatus().name(), null, null);
                adminNotificationService.createNotification(AdminNotificationType.PAYMENT_COMPLETED, order.getId(), null,
                        "付款金額異常", "訂單 #" + order.getOrderNumber() + " 付款金額 NT$" + response.getAmount()
                                + " 與訂單金額 NT$" + order.getTotalAmount() + " 不符，請人工確認");
                return false;
            }
            
            // 檢查訂單狀態（只允許待付款狀態的訂單進行支付成功處理）
            // 注意：數據庫約束允許：PENDING_PAYMENT, PAID, PROCESSING, COMPLETED, CANCELLED, REFUNDED
            // 支付成功後更新為 PAID（已付款）狀態
            if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
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
            
            // 累計會員消費並通知後台
            try {
                memberService.addTotalSpent(order.getCustomerId(), order.getTotalAmount());
            } catch (Exception e) {
                log.error("Failed to update member total spent for order {}", order.getOrderNumber(), e);
            }
            adminNotificationService.createNotification(AdminNotificationType.PAYMENT_COMPLETED, order.getId(), null,
                    "收款完成", "訂單 #" + order.getOrderNumber() + " 已完成線上付款，金額：NT$" + order.getTotalAmount());

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
            Optional<Order> orderOpt = findOrderForCallback(orderNumber);
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
            orderStockService.release(order.getId(), order.getOrderNumber());
            
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
     * 依回調的訂單編號找訂單。
     * 新交易會透過 CustomField1 回傳完整訂單編號；舊交易的 MerchantTradeNo 可能被截斷，
     * 此時僅在前綴唯一對應一筆訂單時才採用，避免同一分鐘內的訂單互相誤判。
     */
    private Optional<Order> findOrderForCallback(String orderNumber) {
        if (orderNumber == null || orderNumber.isBlank()) {
            return Optional.empty();
        }
        Optional<Order> exact = orderRepository.findByOrderNumber(orderNumber);
        if (exact.isPresent() || orderNumber.length() < 10) {
            return exact;
        }
        List<Order> candidates = orderRepository.findByOrderNumberStartingWith(orderNumber);
        if (candidates.size() == 1) {
            log.info("Found order by unique prefix match: {} -> {}", orderNumber, candidates.get(0).getOrderNumber());
            return Optional.of(candidates.get(0));
        }
        if (candidates.size() > 1) {
            log.error("Ambiguous order number prefix {} matches {} orders; manual reconciliation required",
                    orderNumber, candidates.size());
        }
        return Optional.empty();
    }

    /**
     * 綠界 TotalAmount 以整數送出，比對時以訂單金額的整數部分為準
     */
    private static boolean amountMatches(Order order, BigDecimal paidAmount) {
        if (paidAmount == null || order.getTotalAmount() == null) {
            return true;
        }
        BigDecimal expected = order.getTotalAmount().setScale(0, RoundingMode.DOWN);
        return paidAmount.setScale(0, RoundingMode.DOWN).compareTo(expected) == 0;
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
