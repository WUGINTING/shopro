package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.RewardClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardClaimRepository extends JpaRepository<RewardClaim, Long> {
    
    Page<RewardClaim> findByMemberId(Long memberId, Pageable pageable);
    
    List<RewardClaim> findByRewardConfigId(Long rewardConfigId);
    
    boolean existsByMemberIdAndRewardConfigId(Long memberId, Long rewardConfigId);
    
    List<RewardClaim> findByMemberIdAndRewardType(Long memberId, String rewardType);
}
