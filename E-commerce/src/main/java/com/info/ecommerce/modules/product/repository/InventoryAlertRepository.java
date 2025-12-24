package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.InventoryAlert;
import com.info.ecommerce.modules.product.enums.AlertLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAlertRepository extends JpaRepository<InventoryAlert, Long> {
    
    List<InventoryAlert> findByResolvedFalse();
    
    List<InventoryAlert> findByProductIdAndResolvedFalse(Long productId);
    
    List<InventoryAlert> findByAlertLevelAndResolvedFalse(AlertLevel alertLevel);
}
