package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.MemberGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberGroupMappingRepository extends JpaRepository<MemberGroupMapping, Long> {
    
    List<MemberGroupMapping> findByMemberId(Long memberId);
    
    List<MemberGroupMapping> findByGroupId(Long groupId);
    
    boolean existsByMemberIdAndGroupId(Long memberId, Long groupId);
    
    void deleteByMemberIdAndGroupId(Long memberId, Long groupId);
    
    void deleteByMemberId(Long memberId);
}
