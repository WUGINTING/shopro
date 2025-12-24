package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.entity.OrderNotification;
import com.info.ecommerce.modules.order.enums.NotificationType;
import com.info.ecommerce.modules.order.repository.OrderNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 訂單通知服務 - Email/SMS 通知
 * 注意：此為簡化版本，實際應該整合真實的郵件和簡訊服務
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final OrderNotificationRepository orderNotificationRepository;

    /**
     * 發送訂單通知
     */
    @Transactional
    public void sendNotification(Long orderId, NotificationType type, String recipient, 
                                String subject, String content) {
        OrderNotification notification = OrderNotification.builder()
            .orderId(orderId)
            .notificationType(type)
            .recipient(recipient)
            .subject(subject)
            .content(content)
            .isSent(false)
            .build();
        
        try {
            // 這裡應該調用實際的郵件或簡訊服務
            // 目前僅記錄日誌
            if (type == NotificationType.EMAIL) {
                log.info("發送郵件通知到 {} - 主旨: {}", recipient, subject);
                // TODO: 整合郵件服務
            } else if (type == NotificationType.SMS) {
                log.info("發送簡訊通知到 {} - 內容: {}", recipient, content);
                // TODO: 整合簡訊服務
            }
            
            notification.setIsSent(true);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("發送通知失敗: {}", e.getMessage());
            notification.setIsSent(false);
            notification.setErrorMessage(e.getMessage());
        }
        
        orderNotificationRepository.save(notification);
    }

    /**
     * 發送訂單狀態變更通知
     */
    @Transactional
    public void sendOrderStatusChangeNotification(Long orderId, String customerEmail, 
                                                  String orderNumber, String newStatus) {
        String subject = "訂單狀態更新通知 - " + orderNumber;
        String content = String.format("您的訂單 %s 狀態已更新為：%s", orderNumber, newStatus);
        
        sendNotification(orderId, NotificationType.EMAIL, customerEmail, subject, content);
    }

    /**
     * 發送訂單建立通知
     */
    @Transactional
    public void sendOrderCreatedNotification(Long orderId, String customerEmail, String orderNumber) {
        String subject = "訂單建立成功 - " + orderNumber;
        String content = String.format("感謝您的訂購！您的訂單 %s 已成功建立。", orderNumber);
        
        sendNotification(orderId, NotificationType.EMAIL, customerEmail, subject, content);
    }

    /**
     * 發送門市取貨通知
     */
    @Transactional
    public void sendStorePickupNotification(Long orderId, String customerPhone, 
                                           String orderNumber, String storeName) {
        String content = String.format("您的訂單 %s 已到達 %s，請攜帶證件前往取貨。", orderNumber, storeName);
        
        sendNotification(orderId, NotificationType.SMS, customerPhone, null, content);
    }

    /**
     * 重新發送未成功的通知
     */
    @Transactional
    public void retryFailedNotifications() {
        List<OrderNotification> failedNotifications = orderNotificationRepository.findByIsSent(false);
        
        for (OrderNotification notification : failedNotifications) {
            sendNotification(
                notification.getOrderId(),
                notification.getNotificationType(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getContent()
            );
        }
    }

    /**
     * 取得訂單的所有通知記錄
     */
    @Transactional(readOnly = true)
    public List<OrderNotification> getNotificationsByOrderId(Long orderId) {
        return orderNotificationRepository.findByOrderId(orderId);
    }
}
