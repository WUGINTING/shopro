package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.OrderQA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderQARepository extends JpaRepository<OrderQA, Long> {
    
    List<OrderQA> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    
    Page<OrderQA> findByOrderId(Long orderId, Pageable pageable);
    
    List<OrderQA> findByAskerId(Long askerId);
}
