package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.system.dto.PaymentConfigDTO;
import com.info.ecommerce.modules.system.entity.PaymentConfig;
import com.info.ecommerce.modules.system.repository.PaymentConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 金流設定服務
 */
@Service
@RequiredArgsConstructor
public class PaymentConfigService {

    private final PaymentConfigRepository paymentConfigRepository;

    /**
     * 創建金流設定
     */
    @Transactional
    public PaymentConfigDTO createPaymentConfig(PaymentConfigDTO dto) {
        PaymentConfig config = new PaymentConfig();
        BeanUtils.copyProperties(dto, config, "id");
        config = paymentConfigRepository.save(config);
        return toDTO(config);
    }

    /**
     * 更新金流設定
     */
    @Transactional
    public PaymentConfigDTO updatePaymentConfig(Long id, PaymentConfigDTO dto) {
        PaymentConfig config = paymentConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("金流設定不存在"));
        
        BeanUtils.copyProperties(dto, config, "id", "createdAt", "updatedAt");
        config = paymentConfigRepository.save(config);
        return toDTO(config);
    }

    /**
     * 取得金流設定詳情
     */
    public PaymentConfigDTO getPaymentConfig(Long id) {
        PaymentConfig config = paymentConfigRepository.findById(id)
                .orElseThrow(() -> new BusinessException("金流設定不存在"));
        return toDTO(config);
    }

    /**
     * 刪除金流設定
     */
    @Transactional
    public void deletePaymentConfig(Long id) {
        if (!paymentConfigRepository.existsById(id)) {
            throw new BusinessException("金流設定不存在");
        }
        paymentConfigRepository.deleteById(id);
    }

    /**
     * 分頁查詢金流設定
     */
    public Page<PaymentConfigDTO> listPaymentConfigs(Pageable pageable) {
        return paymentConfigRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 查詢已啟用的金流設定
     */
    public List<PaymentConfigDTO> listEnabledPaymentConfigs() {
        return paymentConfigRepository.findByEnabledOrderBySortOrderAsc(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 依付款方式查詢
     */
    public List<PaymentConfigDTO> listByPaymentMethod(String paymentMethod) {
        return paymentConfigRepository.findByPaymentMethod(paymentMethod)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 實體轉 DTO
     */
    private PaymentConfigDTO toDTO(PaymentConfig entity) {
        PaymentConfigDTO dto = new PaymentConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
