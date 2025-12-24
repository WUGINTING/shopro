package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberLevelRepository extends JpaRepository<MemberLevel, Long> {
    
    Optional<MemberLevel> findByName(String name);
    
    boolean existsByName(String name);
    
    List<MemberLevel> findByEnabledOrderByLevelOrderAsc(Boolean enabled);
    
    List<MemberLevel> findAllByOrderByLevelOrderAsc();
}
