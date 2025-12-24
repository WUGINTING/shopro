package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.EdmCampaign;
import com.info.ecommerce.modules.crm.enums.EdmStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EdmCampaignRepository extends JpaRepository<EdmCampaign, Long> {
    
    Page<EdmCampaign> findByStatus(EdmStatus status, Pageable pageable);
    
    List<EdmCampaign> findByStatusAndScheduledAtBefore(EdmStatus status, LocalDateTime dateTime);
    
    Page<EdmCampaign> findByTargetGroupId(Long targetGroupId, Pageable pageable);
}
