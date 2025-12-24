package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.MemberGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long> {
    
    Optional<MemberGroup> findByName(String name);
    
    boolean existsByName(String name);
    
    List<MemberGroup> findByEnabled(Boolean enabled);
}
