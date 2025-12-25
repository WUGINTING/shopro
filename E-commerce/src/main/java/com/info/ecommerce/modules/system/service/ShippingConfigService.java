package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.system.dto.ShippingConfigDTO;
import com.info.ecommerce.modules.system.entity.ShippingConfig;
import com.info.ecommerce.modules.system.repository.ShippingConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物流設定服務
 */
@Service
@RequiredArgsConstructor
public class ShippingConfigService {

    private final ShippingConfigRepository shippingConfigRepository;

    /**
     * 創建物流設定
     */
    @Transactional
    public ShippingConfigDTO createShippingConfig(ShippingConfigDTO dto) {
        ShippingConfig config = new ShippingConfig();
        BeanUtils.copyProperties(dto, config, "id");
        config = shippingConfigRepository.save(config);
        return toDTO(config);
    }

    /**
     * 更新物流設定
     */
    @Transactional
    public ShippingConfigDTO updateShippingConfig(Long id, ShippingConfigDTO dto) {
        ShippingConfig config = shippingConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("物流設定不存在"));
        
        BeanUtils.copyProperties(dto, config, "id", "createdAt", "updatedAt");
        config = shippingConfigRepository.save(config);
        return toDTO(config);
    }

    /**
     * 取得物流設定詳情
     */
    public ShippingConfigDTO getShippingConfig(Long id) {
        ShippingConfig config = shippingConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("物流設定不存在"));
        return toDTO(config);
    }

    /**
     * 刪除物流設定
     */
    @Transactional
    public void deleteShippingConfig(Long id) {
        if (!shippingConfigRepository.existsById(id)) {
            throw new BusinessException("物流設定不存在");
        }
        shippingConfigRepository.deleteById(id);
    }

    /**
     * 分頁查詢物流設定
     */
    public Page<ShippingConfigDTO> listShippingConfigs(Pageable pageable) {
        return shippingConfigRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 查詢已啟用的物流設定
     */
    public List<ShippingConfigDTO> listEnabledShippingConfigs() {
        return shippingConfigRepository.findByEnabledOrderBySortOrderAsc(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 依配送方式查詢
     */
    public List<ShippingConfigDTO> listByShippingMethod(String shippingMethod) {
        return shippingConfigRepository.findByShippingMethod(shippingMethod)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 實體轉 DTO
     */
    private ShippingConfigDTO toDTO(ShippingConfig entity) {
        ShippingConfigDTO dto = new ShippingConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
