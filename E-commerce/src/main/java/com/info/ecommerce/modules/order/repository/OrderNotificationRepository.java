package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.OrderNotification;
import com.info.ecommerce.modules.order.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderNotificationRepository extends JpaRepository<OrderNotification, Long> {
    
    List<OrderNotification> findByOrderId(Long orderId);
    
    List<OrderNotification> findByIsSent(Boolean isSent);
    
    List<OrderNotification> findByNotificationType(NotificationType notificationType);
}
