package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 查詢用戶的未讀通知
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    // 查詢系統通知（userId 為 null）的未讀通知
    List<Notification> findByUserIdIsNullAndIsReadFalseOrderByCreatedAtDesc();
    
    // 查詢用戶的所有通知（包括已讀）
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 查詢系統通知
    List<Notification> findByUserIdIsNullOrderByCreatedAtDesc();
    
    // 統計未讀通知數量
    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.userId = :userId OR n.userId IS NULL) AND n.isRead = false")
    Long countUnreadByUserId(Long userId);
    
    // 統計系統未讀通知數量
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId IS NULL AND n.isRead = false")
    Long countUnreadSystemNotifications();
}

