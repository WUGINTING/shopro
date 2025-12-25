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
                .siteName("Shopro 電商平台")
                .defaultCurrency("TWD")
                .taxRate(java.math.BigDecimal.valueOf(5.00))
                .autoOrderCancelMinutes(30)
                .stockDeductionTiming("PAYMENT_COMPLETED")
                .requireMemberApproval(false)
                .pointsExpirationDays(365)
                .minPasswordLength(8)
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
