package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    List<ProductImage> findByProductId(Long productId);
    
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);
    
    ProductImage findByProductIdAndIsPrimaryTrue(Long productId);
}
