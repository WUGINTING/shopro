package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.AdminNotification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {

    List<AdminNotification> findTop20ByOrderByCreatedAtDesc();

    List<AdminNotification> findTop20ByReadFalseOrderByCreatedAtDesc();

    /**
     * 計算未讀通知數量
     */
    long countByReadFalse();

    /**
     * 取得所有未讀通知
     */
    List<AdminNotification> findByReadFalse();
}
