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
}
