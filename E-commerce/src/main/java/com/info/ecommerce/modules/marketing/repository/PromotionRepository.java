package com.info.ecommerce.modules.marketing.repository;

import com.info.ecommerce.modules.marketing.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    Page<Promotion> findByEnabled(Boolean enabled, Pageable pageable);
    
    Page<Promotion> findByType(String type, Pageable pageable);
    
    Page<Promotion> findByNameContaining(String name, Pageable pageable);
}

