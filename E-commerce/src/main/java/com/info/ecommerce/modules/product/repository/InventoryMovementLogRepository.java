package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.InventoryMovementLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementLogRepository extends JpaRepository<InventoryMovementLog, Long> {

    List<InventoryMovementLog> findTop100ByOrderByCreatedAtDesc();

    List<InventoryMovementLog> findTop100ByProductIdOrderByCreatedAtDesc(Long productId);
}
