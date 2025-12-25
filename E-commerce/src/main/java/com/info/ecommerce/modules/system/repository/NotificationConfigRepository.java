package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.NotificationConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfig, Long> {
    
    List<NotificationConfig> findByEnabledOrderBySortOrderAsc(Boolean enabled);
    
    Optional<NotificationConfig> findByNotificationTypeAndEventType(String notificationType, String eventType);
    
    Page<NotificationConfig> findByNotificationType(String notificationType, Pageable pageable);
    
    List<NotificationConfig> findByEventType(String eventType);
}
