package com.info.ecommerce.modules.payment.repository;

import com.info.ecommerce.modules.payment.entity.PaymentGatewayTransaction;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentGatewayTransactionRepository extends JpaRepository<PaymentGatewayTransaction, Long> {
    
    Optional<PaymentGatewayTransaction> findByTransactionId(String transactionId);
    
    Optional<PaymentGatewayTransaction> findByOrderNumber(String orderNumber);
    
    List<PaymentGatewayTransaction> findByGatewayAndStatus(PaymentGateway gateway, PaymentGatewayStatus status);
    
    List<PaymentGatewayTransaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM PaymentGatewayTransaction t WHERE t.orderNumber LIKE %:keyword% OR t.transactionId LIKE %:keyword%")
    List<PaymentGatewayTransaction> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(t) FROM PaymentGatewayTransaction t WHERE (:status IS NULL OR t.status = :status) AND t.createdAt >= :startDate")
    Long countByStatusAndCreatedAtAfter(@Param("status") PaymentGatewayStatus status, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(t.amount) FROM PaymentGatewayTransaction t WHERE (:status IS NULL OR t.status = :status) AND t.createdAt >= :startDate")
    java.math.BigDecimal sumAmountByStatusAndCreatedAtAfter(@Param("status") PaymentGatewayStatus status, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT t.gateway, COUNT(t), SUM(t.amount) FROM PaymentGatewayTransaction t WHERE t.status = 'SUCCESS' AND t.createdAt >= :startDate GROUP BY t.gateway")
    List<Object[]> getGatewayStatistics(@Param("startDate") LocalDateTime startDate);
}
