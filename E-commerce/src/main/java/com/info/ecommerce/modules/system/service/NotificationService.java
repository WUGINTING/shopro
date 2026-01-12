package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.product.entity.InventoryAlert;
import com.info.ecommerce.modules.product.enums.AlertLevel;
import com.info.ecommerce.modules.product.repository.InventoryAlertRepository;
import com.info.ecommerce.modules.system.dto.NotificationDTO;
import com.info.ecommerce.modules.system.entity.Notification;
import com.info.ecommerce.modules.system.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服務
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final InventoryAlertRepository inventoryAlertRepository;

    /**
     * 獲取用戶的未讀通知
     */
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        List<Notification> systemNotifications = notificationRepository
                .findByUserIdIsNullAndIsReadFalseOrderByCreatedAtDesc();
        
        notifications.addAll(systemNotifications);
        return notifications.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(20)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 獲取未讀通知數量
     */
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * 標記通知為已讀
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        
        // 檢查權限：只能標記自己的通知或系統通知
        if (notification.getUserId() != null && !notification.getUserId().equals(userId)) {
            throw new RuntimeException("無權限操作此通知");
        }
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    /**
     * 標記所有通知為已讀
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        List<Notification> systemNotifications = notificationRepository
                .findByUserIdIsNullAndIsReadFalseOrderByCreatedAtDesc();
        
        notifications.addAll(systemNotifications);
        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : notifications) {
            notification.setIsRead(true);
            notification.setReadAt(now);
        }
        notificationRepository.saveAll(notifications);
    }

    /**
     * 創建通知
     */
    @Transactional
    public NotificationDTO createNotification(NotificationDTO dto) {
        Notification notification = new Notification();
        BeanUtils.copyProperties(dto, notification, "id", "readAt");
        notification = notificationRepository.save(notification);
        return toDTO(notification);
    }

    /**
     * 定期檢查並創建通知
     * 每 5 分鐘執行一次
     */
    @Scheduled(fixedRate = 300000) // 5 分鐘
    @Transactional
    public void checkAndCreateNotifications() {
        try {
            // 檢查待處理訂單
            checkPendingOrders();
            
            // 檢查庫存警示
            checkInventoryAlerts();
        } catch (Exception e) {
            log.error("檢查通知失敗", e);
        }
    }

    /**
     * 檢查待處理訂單
     */
    private void checkPendingOrders() {
        try {
            // 查詢待付款訂單
            long pendingPaymentCount = orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT);
            if (pendingPaymentCount > 0) {
                createSystemNotificationIfNotExists(
                    "ORDER",
                    "待付款訂單",
                    String.format("目前有 %d 筆訂單等待付款", pendingPaymentCount),
                    "shopping_bag",
                    "warning",
                    "HIGH"
                );
            }

            // 查詢已付款待處理訂單
            long paidCount = orderRepository.countByStatus(OrderStatus.PAID);
            if (paidCount > 0) {
                createSystemNotificationIfNotExists(
                    "ORDER",
                    "待處理訂單",
                    String.format("目前有 %d 筆訂單已付款，等待處理", paidCount),
                    "shopping_cart",
                    "primary",
                    "MEDIUM"
                );
            }

            // 查詢處理中訂單
            long processingCount = orderRepository.countByStatus(OrderStatus.PROCESSING);
            if (processingCount > 0) {
                createSystemNotificationIfNotExists(
                    "ORDER",
                    "處理中訂單",
                    String.format("目前有 %d 筆訂單正在處理中", processingCount),
                    "local_shipping",
                    "info",
                    "LOW"
                );
            }
        } catch (Exception e) {
            log.error("檢查訂單通知失敗", e);
        }
    }

    /**
     * 檢查庫存警示
     */
    private void checkInventoryAlerts() {
        List<InventoryAlert> unresolvedAlerts = inventoryAlertRepository.findByResolvedFalse();
        if (!unresolvedAlerts.isEmpty()) {
            long criticalCount = unresolvedAlerts.stream()
                    .filter(a -> a.getAlertLevel() == AlertLevel.CRITICAL || a.getAlertLevel() == AlertLevel.OUT_OF_STOCK)
                    .count();
            
            if (criticalCount > 0) {
                createSystemNotificationIfNotExists(
                    "INVENTORY",
                    "庫存警示",
                    String.format("有 %d 個商品庫存嚴重不足或已缺貨", criticalCount),
                    "warning",
                    "negative",
                    "HIGH"
                );
            } else {
                createSystemNotificationIfNotExists(
                    "INVENTORY",
                    "庫存警示",
                    String.format("有 %d 個商品庫存偏低", unresolvedAlerts.size()),
                    "inventory",
                    "warning",
                    "MEDIUM"
                );
            }
        }
    }

    /**
     * 如果通知不存在則創建系統通知
     */
    private void createSystemNotificationIfNotExists(String type, String title, String content, 
                                                     String icon, String color, String priority) {
        // 檢查最近 1 小時內是否已有相同類型的未讀通知
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Notification> recentNotifications = notificationRepository
                .findByUserIdIsNullAndIsReadFalseOrderByCreatedAtDesc();
        
        boolean exists = recentNotifications.stream()
                .anyMatch(n -> n.getType().equals(type) 
                        && n.getTitle().equals(title)
                        && n.getCreatedAt().isAfter(oneHourAgo));
        
        if (!exists) {
            Notification notification = Notification.builder()
                    .userId(null) // 系統通知
                    .type(type)
                    .title(title)
                    .content(content)
                    .icon(icon)
                    .color(color)
                    .priority(priority)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        }
    }

    /**
     * 實體轉 DTO
     */
    private NotificationDTO toDTO(Notification entity) {
        NotificationDTO dto = new NotificationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}

