package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.system.dto.SystemConfigDTO;
import com.info.ecommerce.modules.system.entity.SystemConfig;
import com.info.ecommerce.modules.system.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系統基本設定服務
 */
@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    
    // Default configuration constants
    private static final String DEFAULT_SITE_NAME = "Shopro 電商平台";
    private static final String DEFAULT_CURRENCY = "TWD";
    private static final java.math.BigDecimal DEFAULT_TAX_RATE = java.math.BigDecimal.valueOf(5.00);
    private static final Integer DEFAULT_ORDER_CANCEL_MINUTES = 30;
    private static final String DEFAULT_STOCK_DEDUCTION_TIMING = "PAYMENT_COMPLETED";
    private static final Boolean DEFAULT_REQUIRE_MEMBER_APPROVAL = false;
    private static final Integer DEFAULT_POINTS_EXPIRATION_DAYS = 365;
    private static final Integer DEFAULT_MIN_PASSWORD_LENGTH = 8;

    /**
     * 取得系統設定（只有一筆）
     */
    public SystemConfigDTO getSystemConfig() {
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdDesc()
                .orElse(null);
        
        if (config == null) {
            // 返回預設值
            return createDefaultConfig();
        }
        
        return toDTO(config);
    }

    /**
     * 更新或創建系統設定
     */
    @Transactional
    public SystemConfigDTO saveSystemConfig(SystemConfigDTO dto) {
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdDesc()
                .orElse(new SystemConfig());
        
        BeanUtils.copyProperties(dto, config, "id", "createdAt", "updatedAt");
        config = systemConfigRepository.save(config);
        return toDTO(config);
    }

    /**
     * 創建預設設定
     */
    private SystemConfigDTO createDefaultConfig() {
        return SystemConfigDTO.builder()
                .siteName(DEFAULT_SITE_NAME)
                .defaultCurrency(DEFAULT_CURRENCY)
                .taxRate(DEFAULT_TAX_RATE)
                .autoOrderCancelMinutes(DEFAULT_ORDER_CANCEL_MINUTES)
                .stockDeductionTiming(DEFAULT_STOCK_DEDUCTION_TIMING)
                .requireMemberApproval(DEFAULT_REQUIRE_MEMBER_APPROVAL)
                .pointsExpirationDays(DEFAULT_POINTS_EXPIRATION_DAYS)
                .minPasswordLength(DEFAULT_MIN_PASSWORD_LENGTH)
                .build();
    }

    /**
     * 實體轉 DTO
     */
    private SystemConfigDTO toDTO(SystemConfig entity) {
        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
