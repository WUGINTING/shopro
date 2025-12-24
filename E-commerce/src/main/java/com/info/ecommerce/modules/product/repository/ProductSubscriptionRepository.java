package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSubscriptionRepository extends JpaRepository<ProductSubscription, Long> {
    
    Optional<ProductSubscription> findByProductId(Long productId);
}
