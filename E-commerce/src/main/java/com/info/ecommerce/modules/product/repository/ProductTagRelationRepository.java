package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductTagRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTagRelationRepository extends JpaRepository<ProductTagRelation, Long> {
    
    List<ProductTagRelation> findByProductId(Long productId);
    
    List<ProductTagRelation> findByTagId(Long tagId);
    
    void deleteByProductId(Long productId);
    
    void deleteByTagId(Long tagId);
    
    Optional<ProductTagRelation> findByProductIdAndTagId(Long productId, Long tagId);
}
