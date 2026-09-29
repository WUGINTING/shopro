package com.info.ecommerce.modules.marketing.repository;

import com.info.ecommerce.modules.marketing.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    Page<Promotion> findByEnabled(Boolean enabled, Pageable pageable);
    
    Page<Promotion> findByType(String type, Pageable pageable);
    
    Page<Promotion> findByNameContaining(String name, Pageable pageable);

    /** 目前進行中的促銷（已啟用且在活動期間內） */
    @Query("SELECT p FROM Promotion p WHERE p.enabled = true "
            + "AND (p.startDate IS NULL OR p.startDate <= :today) AND (p.endDate IS NULL OR p.endDate >= :today)")
    Page<Promotion> findCurrent(@Param("today") java.time.LocalDate today, Pageable pageable);
}

