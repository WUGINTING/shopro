package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Page<Member> findByStatus(MemberStatus status, Pageable pageable);
    
    Page<Member> findByLevelId(Long levelId, Pageable pageable);
    
    Page<Member> findByNameContaining(String name, Pageable pageable);
}
