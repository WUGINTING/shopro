package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    
    Page<Product> findBySalesMode(ProductSalesMode salesMode, Pageable pageable);
    
    List<Product> findBySkuIn(List<String> skus);
    
    Page<Product> findByNameContaining(String name, Pageable pageable);
    
    boolean existsBySku(String sku);
    
    java.util.Optional<Product> findBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, Long id);
    
    Long countByCreatedAtBefore(LocalDateTime date);
}
