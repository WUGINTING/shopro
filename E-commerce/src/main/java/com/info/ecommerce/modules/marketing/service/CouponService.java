package com.info.ecommerce.modules.marketing.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.marketing.dto.CouponDTO;
import com.info.ecommerce.modules.marketing.entity.Coupon;
import com.info.ecommerce.modules.marketing.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * 優惠券管理（後台）
 */
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public static String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase(Locale.ROOT);
    }

    @Transactional
    public CouponDTO create(CouponDTO dto) {
        String code = normalizeCode(dto.getCode());
        if (couponRepository.existsByCode(code)) {
            throw new BusinessException("優惠券代碼已存在");
        }
        validate(dto);
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(dto, coupon, "id", "usedCount", "createdAt", "updatedAt");
        coupon.setCode(code);
        coupon.setUsedCount(0);
        return toDTO(couponRepository.save(coupon));
    }

    @Transactional
    public CouponDTO update(Long id, CouponDTO dto) {
        Coupon coupon = find(id);
        String code = normalizeCode(dto.getCode());
        if (couponRepository.existsByCodeAndIdNot(code, id)) {
            throw new BusinessException("優惠券代碼已存在");
        }
        validate(dto);
        if (dto.getTotalCount() < coupon.getUsedCount()) {
            throw new BusinessException("總數量不可少於已使用次數（" + coupon.getUsedCount() + "）");
        }
        Boolean enabled = dto.getEnabled() != null ? dto.getEnabled() : coupon.getEnabled();
        BeanUtils.copyProperties(dto, coupon, "id", "usedCount", "createdAt", "updatedAt", "enabled");
        coupon.setCode(code);
        coupon.setEnabled(enabled);
        return toDTO(couponRepository.save(coupon));
    }

    @Transactional(readOnly = true)
    public CouponDTO get(Long id) {
        return toDTO(find(id));
    }

    @Transactional(readOnly = true)
    public Page<CouponDTO> list(Pageable pageable) {
        return couponRepository.findAll(pageable).map(this::toDTO);
    }

    /** 前台優惠頁顯示的優惠券（不含使用次數等內部資料） */
    @Transactional(readOnly = true)
    public java.util.List<CouponDTO> listPublic() {
        return couponRepository.findPublicAvailable(java.time.LocalDate.now()).stream()
                .map(coupon -> CouponDTO.builder()
                        .id(coupon.getId())
                        .code(coupon.getCode())
                        .name(coupon.getName())
                        .type(coupon.getType())
                        .discountValue(coupon.getDiscountValue())
                        .minPurchaseAmount(coupon.getMinPurchaseAmount())
                        .maxDiscountAmount(coupon.getMaxDiscountAmount())
                        .validFrom(coupon.getValidFrom())
                        .validUntil(coupon.getValidUntil())
                        .applicable(coupon.getApplicable())
                        .build())
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Coupon coupon = find(id);
        if (coupon.getUsedCount() != null && coupon.getUsedCount() > 0) {
            throw new BusinessException("已被使用的優惠券不可刪除，請改為停用");
        }
        couponRepository.delete(coupon);
    }

    @Transactional
    public CouponDTO setEnabled(Long id, boolean enabled) {
        Coupon coupon = find(id);
        coupon.setEnabled(enabled);
        return toDTO(couponRepository.save(coupon));
    }

    private Coupon find(Long id) {
        return couponRepository.findById(id).orElseThrow(() -> new BusinessException("優惠券不存在"));
    }

    private void validate(CouponDTO dto) {
        if (dto.getValidUntil().isBefore(dto.getValidFrom())) {
            throw new BusinessException("過期日期不可早於生效日期");
        }
        BigDecimal value = dto.getDiscountValue() == null ? BigDecimal.ZERO : dto.getDiscountValue();
        switch (dto.getType()) {
            case "PERCENTAGE" -> {
                if (value.signum() <= 0 || value.compareTo(BigDecimal.valueOf(100)) >= 0) {
                    throw new BusinessException("百分比折扣須介於 0 到 100 之間（例如 10 表示打 9 折）");
                }
            }
            case "FIXED" -> {
                if (value.signum() <= 0) {
                    throw new BusinessException("請輸入折抵金額");
                }
            }
            default -> dto.setDiscountValue(null);
        }
    }

    private CouponDTO toDTO(Coupon coupon) {
        CouponDTO dto = new CouponDTO();
        BeanUtils.copyProperties(coupon, dto);
        return dto;
    }
}
