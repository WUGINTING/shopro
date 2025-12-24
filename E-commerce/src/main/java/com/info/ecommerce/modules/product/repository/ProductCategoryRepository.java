package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    List<ProductCategory> findByParentId(Long parentId);
    
    List<ProductCategory> findByParentIdIsNull();
    
    List<ProductCategory> findByEnabledTrue();
}
