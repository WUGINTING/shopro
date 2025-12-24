package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.OrderHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    
    List<OrderHistory> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    
    Page<OrderHistory> findByOrderId(Long orderId, Pageable pageable);
    
    List<OrderHistory> findByOperatorId(Long operatorId);
}
