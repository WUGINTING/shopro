package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.EdmSendLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdmSendLogRepository extends JpaRepository<EdmSendLog, Long> {
    
    Page<EdmSendLog> findByCampaignId(Long campaignId, Pageable pageable);
    
    List<EdmSendLog> findByMemberId(Long memberId);
    
    @Query("SELECT COUNT(e) FROM EdmSendLog e WHERE e.campaignId = :campaignId AND e.success = true")
    long countSuccessByCampaignId(Long campaignId);
    
    @Query("SELECT COUNT(e) FROM EdmSendLog e WHERE e.campaignId = :campaignId AND e.success = false")
    long countFailureByCampaignId(Long campaignId);
}
