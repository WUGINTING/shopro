package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.modules.system.dto.AdminNotificationDTO;
import com.info.ecommerce.modules.system.entity.AdminNotification;
import com.info.ecommerce.modules.system.enums.AdminNotificationType;
import com.info.ecommerce.modules.system.repository.AdminNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final AdminNotificationRepository repository;

    @Transactional
    public AdminNotificationDTO createNotification(AdminNotificationType type, Long orderId,
                                                   Long productId, String title, String message) {
        AdminNotification notification = AdminNotification.builder()
                .type(type)
                .orderId(orderId)
                .productId(productId)
                .title(title)
                .message(message)
                .read(false)
                .build();

        return toDto(repository.save(notification));
    }

    @Transactional(readOnly = true)
    public List<AdminNotificationDTO> listRecentNotifications(int limit, boolean unreadOnly) {
        List<AdminNotification> notifications = unreadOnly
                ? repository.findTop20ByReadFalseOrderByCreatedAtDesc()
                : repository.findTop20ByOrderByCreatedAtDesc();

        return notifications.stream()
                .limit(limit)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long id) {
        repository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            repository.save(notification);
        });
    }

    /**
     * 取得未讀通知數量
     */
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return repository.countByReadFalse();
    }

    /**
     * 全部標記為已讀
     */
    @Transactional
    public void markAllAsRead() {
        List<AdminNotification> unreadNotifications = repository.findByReadFalse();
        unreadNotifications.forEach(notification -> notification.setRead(true));
        repository.saveAll(unreadNotifications);
    }

    private AdminNotificationDTO toDto(AdminNotification entity) {
        return AdminNotificationDTO.builder()
                .id(entity.getId())
                .type(entity.getType())
                .orderId(entity.getOrderId())
                .productId(entity.getProductId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .read(entity.getRead())
                .build();
    }
}
