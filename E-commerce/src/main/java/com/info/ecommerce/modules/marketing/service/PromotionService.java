package com.info.ecommerce.modules.marketing.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.marketing.dto.PromotionDTO;
import com.info.ecommerce.modules.marketing.entity.Promotion;
import com.info.ecommerce.modules.marketing.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    @Transactional
    public PromotionDTO createPromotion(PromotionDTO dto) {
        // 驗證日期
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("結束日期不能早於開始日期");
        }

        Promotion promotion = new Promotion();
        BeanUtils.copyProperties(dto, promotion, "id");
        promotion = promotionRepository.save(promotion);
        return toDTO(promotion);
    }

    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("促銷活動不存在"));

        // 驗證日期
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("結束日期不能早於開始日期");
        }

        BeanUtils.copyProperties(dto, promotion, "id", "createdAt", "updatedAt");
        promotion = promotionRepository.save(promotion);
        return toDTO(promotion);
    }

    public PromotionDTO getPromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("促銷活動不存在"));
        return toDTO(promotion);
    }

    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new BusinessException("促銷活動不存在");
        }
        promotionRepository.deleteById(id);
    }

    public Page<PromotionDTO> listPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<PromotionDTO> listPromotionsByEnabled(Boolean enabled, Pageable pageable) {
        return promotionRepository.findByEnabled(enabled, pageable).map(this::toDTO);
    }

    public Page<PromotionDTO> listPromotionsByType(String type, Pageable pageable) {
        return promotionRepository.findByType(type, pageable).map(this::toDTO);
    }

    @Transactional
    public PromotionDTO enablePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("促銷活動不存在"));
        promotion.setEnabled(true);
        promotion = promotionRepository.save(promotion);
        return toDTO(promotion);
    }

    @Transactional
    public PromotionDTO disablePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("促銷活動不存在"));
        promotion.setEnabled(false);
        promotion = promotionRepository.save(promotion);
        return toDTO(promotion);
    }

    private PromotionDTO toDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        BeanUtils.copyProperties(promotion, dto);
        return dto;
    }
}

