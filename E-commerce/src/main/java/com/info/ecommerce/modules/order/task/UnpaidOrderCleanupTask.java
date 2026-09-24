package com.info.ecommerce.modules.order.task;

import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.order.service.OrderService;
import com.info.ecommerce.modules.order.service.StorefrontCheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 逾期未付款訂單清理：前台選擇「線上付款」但超過期限仍未付款的訂單自動取消並歸還庫存。
 * 貨到付款訂單付款發生在取貨時，不會被取消。
 * 期限由 app.order.unpaid-timeout-hours（環境變數 ORDER_UNPAID_TIMEOUT_HOURS，預設 72 小時，涵蓋 ATM / 超商代碼繳費期限）設定，0 表示停用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UnpaidOrderCleanupTask {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderService orderService;

    @Value("${app.order.unpaid-timeout-hours:72}")
    private long unpaidTimeoutHours;

    @Scheduled(cron = "0 */15 * * * ?")
    public void cancelExpiredUnpaidOrders() {
        if (unpaidTimeoutHours <= 0) {
            return;
        }
        LocalDateTime cutoff = LocalDateTime.now().minusHours(unpaidTimeoutHours);
        List<Order> candidates = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING_PAYMENT, cutoff);
        int cancelled = 0;
        for (Order order : candidates) {
            if (!isOnlinePaymentStorefrontOrder(order.getId())) {
                continue;
            }
            try {
                orderService.updateOrderStatus(order.getId(), OrderStatus.CANCELLED, null, "系統（逾期未付款）");
                cancelled++;
            } catch (Exception e) {
                log.error("Failed to cancel expired unpaid order {}", order.getOrderNumber(), e);
            }
        }
        if (cancelled > 0) {
            log.info("已自動取消 {} 筆逾期未付款訂單（超過 {} 小時）", cancelled, unpaidTimeoutHours);
        }
    }

    private boolean isOnlinePaymentStorefrontOrder(Long orderId) {
        return orderHistoryRepository
                .findByOrderIdAndActionType(orderId, StorefrontCheckoutService.ACTION_STOREFRONT_CHECKOUT)
                .stream()
                .anyMatch(history -> StorefrontCheckoutService.PAYMENT_ECPAY.equals(history.getNewStatus()));
    }
}
