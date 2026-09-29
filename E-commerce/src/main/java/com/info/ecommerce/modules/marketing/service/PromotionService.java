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
        validate(dto);

        Promotion promotion = new Promotion();
        BeanUtils.copyProperties(dto, promotion, "id");
        promotion = promotionRepository.save(promotion);
        return toDTO(promotion);
    }

    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("促銷活動不存在"));

        validate(dto);

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

    /** 前台：只列出進行中的促銷 */
    public Page<PromotionDTO> listCurrentPromotions(Pageable pageable) {
        return promotionRepository.findCurrent(java.time.LocalDate.now(), pageable).map(this::toDTO);
    }

    /** 前台：未啟用或不在活動期間的促銷視為不存在 */
    public PromotionDTO getCurrentPromotion(Long id) {
        java.time.LocalDate today = java.time.LocalDate.now();
        Promotion promotion = promotionRepository.findById(id)
                .filter(p -> Boolean.TRUE.equals(p.getEnabled())
                        && (p.getStartDate() == null || !p.getStartDate().isAfter(today))
                        && (p.getEndDate() == null || !p.getEndDate().isBefore(today)))
                .orElseThrow(() -> new BusinessException("促銷活動不存在"));
        return toDTO(promotion);
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

    /**
     * 促銷活動會直接影響結帳金額，必須設定合理的折扣：
     * 折扣 / 全館活動需指定折扣方式，百分比介於 0 到 100（不含 100），固定金額需大於 0
     */
    private void validate(PromotionDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("結束日期不能早於開始日期");
        }
        String type = dto.getType() == null ? "" : dto.getType().trim();
        if (!java.util.Set.of("DISCOUNT", "FULL_SHOP", "FREE_SHIPPING", "BUY_GIFT").contains(type)) {
            throw new BusinessException("活動類型不正確");
        }
        dto.setType(type);
        if (dto.getMinPurchaseAmount() != null && dto.getMinPurchaseAmount().signum() < 0) {
            throw new BusinessException("最低購買金額不可小於 0");
        }
        if (dto.getMaxDiscountAmount() != null && dto.getMaxDiscountAmount().signum() < 0) {
            throw new BusinessException("最高折抵金額不可小於 0");
        }
        if ("DISCOUNT".equals(type) || "FULL_SHOP".equals(type)) {
            java.math.BigDecimal value = dto.getDiscountValue();
            if ("PERCENTAGE".equals(dto.getDiscountType())) {
                if (value == null || value.signum() <= 0 || value.compareTo(java.math.BigDecimal.valueOf(100)) >= 0) {
                    throw new BusinessException("百分比折扣須介於 0 到 100 之間（例如 10 表示打 9 折）");
                }
            } else if ("FIXED".equals(dto.getDiscountType())) {
                if (value == null || value.signum() <= 0) {
                    throw new BusinessException("請輸入折抵金額");
                }
            } else {
                throw new BusinessException("折扣活動請選擇折扣方式（百分比或固定金額）");
            }
        }
    }
}
