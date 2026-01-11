package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.OrderDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDiscountRepository extends JpaRepository<OrderDiscount, Long> {
    
    List<OrderDiscount> findByOrderId(Long orderId);
    
    List<OrderDiscount> findByDiscountCode(String discountCode);
    
    @Query("SELECT d FROM OrderDiscount d ORDER BY d.createdAt DESC")
    List<OrderDiscount> findAllOrderByCreatedAtDesc();
}
