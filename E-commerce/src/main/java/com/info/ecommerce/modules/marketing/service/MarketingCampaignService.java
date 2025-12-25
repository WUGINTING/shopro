package com.info.ecommerce.modules.marketing.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.marketing.dto.MarketingCampaignDTO;
import com.info.ecommerce.modules.marketing.entity.MarketingCampaign;
import com.info.ecommerce.modules.marketing.enums.CampaignStatus;
import com.info.ecommerce.modules.marketing.enums.CampaignType;
import com.info.ecommerce.modules.marketing.repository.MarketingCampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketingCampaignService {

    private final MarketingCampaignRepository campaignRepository;

    @Transactional
    public MarketingCampaignDTO createCampaign(MarketingCampaignDTO dto) {
        // 驗證日期
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("結束日期不能早於開始日期");
        }

        MarketingCampaign campaign = new MarketingCampaign();
        BeanUtils.copyProperties(dto, campaign, "id");
        campaign = campaignRepository.save(campaign);
        return toDTO(campaign);
    }

    @Transactional
    public MarketingCampaignDTO updateCampaign(Long id, MarketingCampaignDTO dto) {
        MarketingCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("營銷活動不存在"));

        // 驗證日期
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("結束日期不能早於開始日期");
        }

        BeanUtils.copyProperties(dto, campaign, "id", "createdAt", "updatedAt");
        campaign = campaignRepository.save(campaign);
        return toDTO(campaign);
    }

    public MarketingCampaignDTO getCampaign(Long id) {
        MarketingCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("營銷活動不存在"));
        return toDTO(campaign);
    }

    @Transactional
    public void deleteCampaign(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new BusinessException("營銷活動不存在");
        }
        campaignRepository.deleteById(id);
    }

    public Page<MarketingCampaignDTO> listCampaigns(Pageable pageable) {
        return campaignRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<MarketingCampaignDTO> listCampaignsByStatus(CampaignStatus status, Pageable pageable) {
        return campaignRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    public Page<MarketingCampaignDTO> listCampaignsByType(CampaignType type, Pageable pageable) {
        return campaignRepository.findByType(type, pageable).map(this::toDTO);
    }

    @Transactional
    public MarketingCampaignDTO updateCampaignStatus(Long id, CampaignStatus status) {
        MarketingCampaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("營銷活動不存在"));
        campaign.setStatus(status);
        campaign = campaignRepository.save(campaign);
        return toDTO(campaign);
    }

    private MarketingCampaignDTO toDTO(MarketingCampaign campaign) {
        MarketingCampaignDTO dto = new MarketingCampaignDTO();
        BeanUtils.copyProperties(campaign, dto);
        return dto;
    }
}
