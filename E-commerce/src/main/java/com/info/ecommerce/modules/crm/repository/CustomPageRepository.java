package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.CustomPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomPageRepository extends JpaRepository<CustomPage, Long> {
    
    Optional<CustomPage> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    List<CustomPage> findByEnabledOrderBySortOrderAsc(Boolean enabled);
    
    List<CustomPage> findAllByOrderBySortOrderAsc();
}
