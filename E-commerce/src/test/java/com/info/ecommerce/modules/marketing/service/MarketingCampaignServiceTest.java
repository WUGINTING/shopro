package com.info.ecommerce.modules.marketing.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.marketing.dto.MarketingCampaignDTO;
import com.info.ecommerce.modules.marketing.entity.MarketingCampaign;
import com.info.ecommerce.modules.marketing.enums.CampaignStatus;
import com.info.ecommerce.modules.marketing.enums.CampaignType;
import com.info.ecommerce.modules.marketing.repository.MarketingCampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MarketingCampaignService
 */
@ExtendWith(MockitoExtension.class)
class MarketingCampaignServiceTest {

    @Mock
    private MarketingCampaignRepository campaignRepository;

    @InjectMocks
    private MarketingCampaignService campaignService;

    private MarketingCampaign campaign;
    private MarketingCampaignDTO campaignDTO;

    @BeforeEach
    void setUp() {
        campaign = MarketingCampaign.builder()
                .id(1L)
                .name("Test Campaign")
                .description("Test Description")
                .type(CampaignType.DISCOUNT)
                .status(CampaignStatus.DRAFT)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .discountRate(BigDecimal.valueOf(20))
                .minPurchaseAmount(BigDecimal.valueOf(100))
                .build();

        campaignDTO = new MarketingCampaignDTO();
        campaignDTO.setName("Test Campaign");
        campaignDTO.setDescription("Test Description");
        campaignDTO.setType(CampaignType.DISCOUNT);
        campaignDTO.setStatus(CampaignStatus.DRAFT);
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.setEndDate(LocalDate.now().plusDays(7));
        campaignDTO.setDiscountRate(BigDecimal.valueOf(20));
        campaignDTO.setMinPurchaseAmount(BigDecimal.valueOf(100));
    }

    @Test
    void should_CreateCampaign_When_ValidDataProvided() {
        // given
        when(campaignRepository.save(any(MarketingCampaign.class))).thenReturn(campaign);

        // when
        MarketingCampaignDTO result = campaignService.createCampaign(campaignDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Campaign");
        verify(campaignRepository, times(1)).save(any(MarketingCampaign.class));
    }

    @Test
    void should_ThrowBusinessException_When_EndDateBeforeStartDate() {
        // given
        campaignDTO.setEndDate(LocalDate.now().minusDays(1));

        // when & then
        assertThatThrownBy(() -> campaignService.createCampaign(campaignDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("結束日期不能早於開始日期");
        verify(campaignRepository, never()).save(any(MarketingCampaign.class));
    }

    @Test
    void should_UpdateCampaign_When_CampaignExists() {
        // given
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(MarketingCampaign.class))).thenReturn(campaign);

        MarketingCampaignDTO updateDTO = new MarketingCampaignDTO();
        updateDTO.setName("Updated Campaign");
        updateDTO.setType(CampaignType.PROMOTION);
        updateDTO.setStatus(CampaignStatus.ACTIVE);
        updateDTO.setStartDate(LocalDate.now());
        updateDTO.setEndDate(LocalDate.now().plusDays(14));

        // when
        MarketingCampaignDTO result = campaignService.updateCampaign(1L, updateDTO);

        // then
        assertThat(result).isNotNull();
        verify(campaignRepository, times(1)).findById(1L);
        verify(campaignRepository, times(1)).save(any(MarketingCampaign.class));
    }

    @Test
    void should_ThrowBusinessException_When_UpdateNonExistentCampaign() {
        // given
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> campaignService.updateCampaign(1L, campaignDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("營銷活動不存在");
        verify(campaignRepository, times(1)).findById(1L);
        verify(campaignRepository, never()).save(any(MarketingCampaign.class));
    }

    @Test
    void should_GetCampaign_When_CampaignExists() {
        // given
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        // when
        MarketingCampaignDTO result = campaignService.getCampaign(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Campaign");
        verify(campaignRepository, times(1)).findById(1L);
    }

    @Test
    void should_DeleteCampaign_When_CampaignExists() {
        // given
        when(campaignRepository.existsById(1L)).thenReturn(true);

        // when
        campaignService.deleteCampaign(1L);

        // then
        verify(campaignRepository, times(1)).existsById(1L);
        verify(campaignRepository, times(1)).deleteById(1L);
    }

    @Test
    void should_ListCampaigns_When_RequestingPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<MarketingCampaign> campaigns = List.of(campaign);
        Page<MarketingCampaign> page = new PageImpl<>(campaigns, pageable, campaigns.size());
        when(campaignRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<MarketingCampaignDTO> result = campaignService.listCampaigns(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Campaign");
        verify(campaignRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_ListCampaignsByStatus_When_RequestingPageWithStatus() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<MarketingCampaign> campaigns = List.of(campaign);
        Page<MarketingCampaign> page = new PageImpl<>(campaigns, pageable, campaigns.size());
        when(campaignRepository.findByStatus(CampaignStatus.DRAFT, pageable)).thenReturn(page);

        // when
        Page<MarketingCampaignDTO> result = campaignService.listCampaignsByStatus(CampaignStatus.DRAFT, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(campaignRepository, times(1)).findByStatus(CampaignStatus.DRAFT, pageable);
    }

    @Test
    void should_ListCampaignsByType_When_RequestingPageWithType() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<MarketingCampaign> campaigns = List.of(campaign);
        Page<MarketingCampaign> page = new PageImpl<>(campaigns, pageable, campaigns.size());
        when(campaignRepository.findByType(CampaignType.DISCOUNT, pageable)).thenReturn(page);

        // when
        Page<MarketingCampaignDTO> result = campaignService.listCampaignsByType(CampaignType.DISCOUNT, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(campaignRepository, times(1)).findByType(CampaignType.DISCOUNT, pageable);
    }

    @Test
    void should_UpdateCampaignStatus_When_CampaignExists() {
        // given
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(MarketingCampaign.class))).thenReturn(campaign);

        // when
        MarketingCampaignDTO result = campaignService.updateCampaignStatus(1L, CampaignStatus.ACTIVE);

        // then
        assertThat(result).isNotNull();
        verify(campaignRepository, times(1)).findById(1L);
        verify(campaignRepository, times(1)).save(any(MarketingCampaign.class));
    }
}
