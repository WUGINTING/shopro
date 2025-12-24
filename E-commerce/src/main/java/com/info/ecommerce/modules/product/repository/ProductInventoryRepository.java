package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    
    List<ProductInventory> findByProductId(Long productId);
    
    Optional<ProductInventory> findByProductIdAndSpecificationId(Long productId, Long specificationId);
    
    List<ProductInventory> findByWarehouseId(Long warehouseId);
    
    Optional<ProductInventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
