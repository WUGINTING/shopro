package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductPreOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPreOrderRepository extends JpaRepository<ProductPreOrder, Long> {
    
    Optional<ProductPreOrder> findByProductId(Long productId);
}
