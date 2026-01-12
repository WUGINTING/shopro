package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderPayment;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PaymentStatus;
import com.info.ecommerce.modules.order.repository.OrderPaymentRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.order.service.OrderHistoryService;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
            
            // 查找訂單
            Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
            if (orderOpt.isEmpty()) {
                log.error("Order not found for payment success: {}", orderNumber);
                return false;
            }
            
            Order order = orderOpt.get();
            
            // 檢查訂單狀態
            if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
                log.warn("Order {} is not in PENDING_PAYMENT status, current status: {}", 
                        orderNumber, order.getStatus());
                return false;
            }
            
            // 更新訂單狀態
            OrderStatus oldStatus = order.getStatus();
            order.setStatus(OrderStatus.PAID);
            order.setPaymentTime(LocalDateTime.now());
            orderRepository.save(order);
            
            // 建立或更新付款記錄
            OrderPayment payment = createOrUpdatePayment(order, response);
            
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
}
