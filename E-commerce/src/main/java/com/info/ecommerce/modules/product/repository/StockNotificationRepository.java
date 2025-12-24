package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.StockNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {
    
    List<StockNotification> findByProductIdAndNotifiedFalse(Long productId);
    
    List<StockNotification> findByNotifiedFalse();
}
