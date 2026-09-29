package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.entity.OrderNotification;
import com.info.ecommerce.modules.order.enums.NotificationType;
import com.info.ecommerce.modules.order.repository.OrderNotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 訂單通知服務 - Email / SMS
 * <p>Email 透過 Spring Mail 寄送：設定 MAIL_HOST 等環境變數後啟用；未設定時不寄送並如實記錄為未寄出。
 * 簡訊尚未串接服務商，一律記錄為未寄出。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final OrderNotificationRepository orderNotificationRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.store-name:遇日小舖}")
    private String storeName;

    /** 是否已設定郵件伺服器（未設定 spring.mail.host 時不會有 JavaMailSender） */
    public boolean isEmailEnabled() {
        return mailSenderProvider.getIfAvailable() != null;
    }

    /**
     * 發送訂單通知並記錄結果
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
        deliver(notification);
        orderNotificationRepository.save(notification);
    }

    /**
     * 實際寄送，並將結果寫回通知記錄
     */
    private void deliver(OrderNotification notification) {
        try {
            if (notification.getNotificationType() == NotificationType.EMAIL) {
                JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
                if (mailSender == null) {
                    markFailed(notification, "尚未設定郵件伺服器（MAIL_HOST）");
                    return;
                }
                if (notification.getRecipient() == null || notification.getRecipient().isBlank()) {
                    markFailed(notification, "缺少收件人");
                    return;
                }
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
                if (mailFrom != null && !mailFrom.isBlank()) {
                    helper.setFrom(mailFrom, storeName);
                }
                helper.setTo(notification.getRecipient());
                helper.setSubject(notification.getSubject() != null ? notification.getSubject() : storeName + " 訂單通知");
                helper.setText(notification.getContent() != null ? notification.getContent() : "", false);
                mailSender.send(message);
                log.info("已寄送郵件通知至 {} - 主旨: {}", notification.getRecipient(), notification.getSubject());
            } else {
                markFailed(notification, "尚未串接簡訊服務");
                return;
            }
            notification.setIsSent(true);
            notification.setSentAt(LocalDateTime.now());
            notification.setErrorMessage(null);
        } catch (Exception e) {
            log.error("發送通知失敗: {}", e.getMessage());
            markFailed(notification, e.getMessage());
        }
    }

    private static void markFailed(OrderNotification notification, String reason) {
        notification.setIsSent(false);
        notification.setErrorMessage(reason);
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
     * 重新發送未成功的 Email 通知（更新原記錄，不另外新增）
     */
    @Transactional
    public void retryFailedNotifications() {
        if (!isEmailEnabled()) {
            return;
        }
        List<OrderNotification> failedNotifications = orderNotificationRepository.findByIsSent(false);
        for (OrderNotification notification : failedNotifications) {
            if (notification.getNotificationType() != NotificationType.EMAIL) {
                continue;
            }
            deliver(notification);
            orderNotificationRepository.save(notification);
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
