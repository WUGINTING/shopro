package com.info.ecommerce.modules.payment.repository;

import com.info.ecommerce.modules.payment.entity.PaymentCallbackLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付回調記錄 Repository
 */
@Repository
public interface PaymentCallbackLogRepository extends JpaRepository<PaymentCallbackLog, Long> {
    
    /**
     * 根據訂單編號查詢
     */
    List<PaymentCallbackLog> findByOrderNumberOrderByCreatedAtDesc(String orderNumber);
    
    /**
     * 根據交易 ID 查詢
     */
    List<PaymentCallbackLog> findByTransactionIdOrderByCreatedAtDesc(String transactionId);
    
    /**
     * 根據支付閘道類型查詢
     */
    Page<PaymentCallbackLog> findByGatewayOrderByCreatedAtDesc(String gateway, Pageable pageable);
    
    /**
     * 根據狀態查詢
     */
    Page<PaymentCallbackLog> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    /**
     * 根據時間範圍查詢
     */
    @Query("SELECT l FROM PaymentCallbackLog l WHERE l.createdAt BETWEEN :startTime AND :endTime ORDER BY l.createdAt DESC")
    Page<PaymentCallbackLog> findByCreatedAtBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    /**
     * 查詢最近的記錄
     */
    Page<PaymentCallbackLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

