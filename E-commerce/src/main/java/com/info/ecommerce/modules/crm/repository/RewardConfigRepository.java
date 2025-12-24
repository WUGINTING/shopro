package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.RewardConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RewardConfigRepository extends JpaRepository<RewardConfig, Long> {
    
    Optional<RewardConfig> findByRewardType(String rewardType);
    
    List<RewardConfig> findByEnabled(Boolean enabled);
    
    List<RewardConfig> findByRewardTypeAndEnabled(String rewardType, Boolean enabled);
}
