package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.payment.config.EcPayConfig;
import com.info.ecommerce.modules.payment.dto.EcPayConfigDTO;
import com.info.ecommerce.modules.payment.entity.EcPayConfigEntity;
import com.info.ecommerce.modules.payment.repository.EcPayConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ECPay 配置服務
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EcPayConfigService {

    private final EcPayConfigRepository ecPayConfigRepository;
    private final EcPayConfig ecPayConfig;

    /**
     * 取得 ECPay 配置
     * 優先返回啟用的配置，如果沒有則返回第一筆
     */
    public EcPayConfigDTO getConfig() {
        EcPayConfigEntity entity = ecPayConfigRepository.findByEnabledTrue()
                .orElseGet(() -> ecPayConfigRepository.findFirstByOrderByIdAsc()
                        .orElseThrow(() -> new BusinessException("ECPay 配置不存在，請先建立配置")));
        
        return toDTO(entity);
    }

    /**
     * 取得 ECPay 配置（根據 ID）
     */
    public EcPayConfigDTO getConfigById(Long id) {
        EcPayConfigEntity entity = ecPayConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ECPay 配置不存在"));
        return toDTO(entity);
    }

    /**
     * 創建或更新 ECPay 配置
     * 如果 ID 為 null，則創建新配置
     * 如果 ID 存在，則更新現有配置
     */
    @Transactional
    public EcPayConfigDTO saveConfig(EcPayConfigDTO dto) {
        EcPayConfigEntity entity;
        
        if (dto.getId() != null) {
            // 更新現有配置
            entity = ecPayConfigRepository.findById(dto.getId())
                    .orElseThrow(() -> new BusinessException("ECPay 配置不存在"));
            BeanUtils.copyProperties(dto, entity, "id", "createdAt");
        } else {
            // 創建新配置
            entity = new EcPayConfigEntity();
            BeanUtils.copyProperties(dto, entity, "id");
        }
        
        entity = ecPayConfigRepository.save(entity);
        log.info("ECPay 配置已保存: ID={}, MerchantID={}, Enabled={}", 
                entity.getId(), entity.getMerchantId(), entity.getEnabled());
        
        // 如果保存的配置是啟用的，重新載入配置類
        if (entity.getEnabled() != null && entity.getEnabled()) {
            try {
                ecPayConfig.reload();
                log.info("ECPay 配置已重新載入");
            } catch (Exception e) {
                log.error("重新載入 ECPay 配置失敗", e);
            }
        }
        
        return toDTO(entity);
    }

    /**
     * 刪除配置
     */
    @Transactional
    public void deleteConfig(Long id) {
        if (!ecPayConfigRepository.existsById(id)) {
            throw new BusinessException("ECPay 配置不存在");
        }
        ecPayConfigRepository.deleteById(id);
        log.info("ECPay 配置已刪除: ID={}", id);
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private EcPayConfigDTO toDTO(EcPayConfigEntity entity) {
        EcPayConfigDTO dto = new EcPayConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}

