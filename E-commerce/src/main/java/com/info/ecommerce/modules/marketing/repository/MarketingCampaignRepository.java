package com.info.ecommerce.modules.marketing.repository;

import com.info.ecommerce.modules.marketing.entity.MarketingCampaign;
import com.info.ecommerce.modules.marketing.enums.CampaignStatus;
import com.info.ecommerce.modules.marketing.enums.CampaignType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketingCampaignRepository extends JpaRepository<MarketingCampaign, Long> {
    
    Page<MarketingCampaign> findByStatus(CampaignStatus status, Pageable pageable);
    
    Page<MarketingCampaign> findByType(CampaignType type, Pageable pageable);
    
    Page<MarketingCampaign> findByNameContaining(String name, Pageable pageable);
}
