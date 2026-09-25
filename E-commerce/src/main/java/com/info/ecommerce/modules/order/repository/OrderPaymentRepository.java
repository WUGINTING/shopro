package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.OrderPayment;
import com.info.ecommerce.modules.order.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {
    
    List<OrderPayment> findByOrderId(Long orderId);
    
    Optional<OrderPayment> findByTransactionId(String transactionId);
    
    List<OrderPayment> findByPaymentStatus(PaymentStatus paymentStatus);
    
    Optional<OrderPayment> findByOrderIdAndGatewayTransactionId(Long orderId, String gatewayTransactionId);

    /** 指定時間後登記的退款筆數 */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) FROM OrderPayment p WHERE p.refundTime >= :since AND p.refundAmount > 0")
    long countRefundsSince(@org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since);

    /** 指定時間後登記的退款金額（以最後一次退款時間計） */
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.refundAmount), 0) FROM OrderPayment p WHERE p.refundTime >= :since")
    java.math.BigDecimal sumRefundsSince(@org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since);

    /** 期間內登記的退款金額 */
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.refundAmount), 0) FROM OrderPayment p WHERE p.refundTime BETWEEN :from AND :to")
    java.math.BigDecimal sumRefundsBetween(@org.springframework.data.repository.query.Param("from") java.time.LocalDateTime from,
                                           @org.springframework.data.repository.query.Param("to") java.time.LocalDateTime to);
}
