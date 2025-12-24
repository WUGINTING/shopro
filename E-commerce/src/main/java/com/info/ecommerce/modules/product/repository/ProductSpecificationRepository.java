package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {
    
    List<ProductSpecification> findByProductId(Long productId);
    
    Optional<ProductSpecification> findBySku(String sku);
    
    List<ProductSpecification> findByProductIdAndEnabledTrue(Long productId);
}
