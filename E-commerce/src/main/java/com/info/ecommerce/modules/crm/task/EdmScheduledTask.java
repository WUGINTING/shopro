package com.info.ecommerce.modules.crm.task;

import com.info.ecommerce.modules.crm.entity.EdmCampaign;
import com.info.ecommerce.modules.crm.enums.EdmStatus;
import com.info.ecommerce.modules.crm.repository.EdmCampaignRepository;
import com.info.ecommerce.modules.crm.service.EdmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 排程 EDM：時間到時自動發送；無法發送（例如未設定寄信或沒有收件人）時標記為失敗，避免每分鐘重試
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EdmScheduledTask {

    private final EdmCampaignRepository edmCampaignRepository;
    private final EdmService edmService;

    @Scheduled(cron = "30 * * * * ?")
    public void sendDueCampaigns() {
        for (EdmCampaign campaign : edmCampaignRepository.findByStatusAndScheduledAtBefore(EdmStatus.SCHEDULED, LocalDateTime.now())) {
            try {
                edmService.sendEdmCampaign(campaign.getId());
                log.info("Scheduled EDM campaign {} sent", campaign.getId());
            } catch (Exception e) {
                log.warn("Scheduled EDM campaign {} could not be sent: {}", campaign.getId(), e.getMessage());
                edmCampaignRepository.findById(campaign.getId()).ifPresent(failed -> {
                    failed.setStatus(EdmStatus.FAILED);
                    edmCampaignRepository.save(failed);
                });
            }
        }
    }
}
