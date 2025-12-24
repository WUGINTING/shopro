package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.CustomerBlacklistDTO;
import com.info.ecommerce.modules.order.entity.CustomerBlacklist;
import com.info.ecommerce.modules.order.repository.CustomerBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 顧客黑名單服務 - 黑名單管理
 */
@Service
@RequiredArgsConstructor
public class CustomerBlacklistService {

    private final CustomerBlacklistRepository customerBlacklistRepository;

    /**
     * 新增顧客到黑名單
     */
    @Transactional
    public CustomerBlacklistDTO addToBlacklist(CustomerBlacklistDTO dto) {
        // 檢查是否已存在
        if (customerBlacklistRepository.existsByCustomerIdAndIsActive(dto.getCustomerId(), true)) {
            throw new RuntimeException("此客戶已在黑名單中");
        }
        
        CustomerBlacklist blacklist = convertToEntity(dto);
        blacklist.setIsActive(true);
        blacklist = customerBlacklistRepository.save(blacklist);
        
        return convertToDTO(blacklist);
    }

    /**
     * 從黑名單移除（設為不啟用）
     */
    @Transactional
    public CustomerBlacklistDTO removeFromBlacklist(Long blacklistId) {
        CustomerBlacklist blacklist = customerBlacklistRepository.findById(blacklistId)
            .orElseThrow(() -> new RuntimeException("黑名單記錄不存在"));
        
        blacklist.setIsActive(false);
        blacklist = customerBlacklistRepository.save(blacklist);
        
        return convertToDTO(blacklist);
    }

    /**
     * 檢查顧客是否在黑名單中
     */
    @Transactional(readOnly = true)
    public boolean isBlacklisted(Long customerId) {
        return customerBlacklistRepository.existsByCustomerIdAndIsActive(customerId, true);
    }

    /**
     * 取得所有啟用的黑名單
     */
    @Transactional(readOnly = true)
    public List<CustomerBlacklistDTO> getActiveBlacklist() {
        return customerBlacklistRepository.findByIsActive(true)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 分頁取得黑名單
     */
    @Transactional(readOnly = true)
    public Page<CustomerBlacklistDTO> getBlacklistPage(Pageable pageable) {
        return customerBlacklistRepository.findAll(pageable)
            .map(this::convertToDTO);
    }

    /**
     * 取得顧客的黑名單記錄
     */
    @Transactional(readOnly = true)
    public CustomerBlacklistDTO getByCustomerId(Long customerId) {
        CustomerBlacklist blacklist = customerBlacklistRepository
            .findByCustomerIdAndIsActive(customerId, true)
            .orElseThrow(() -> new RuntimeException("找不到黑名單記錄"));
        return convertToDTO(blacklist);
    }

    /**
     * 更新黑名單原因
     */
    @Transactional
    public CustomerBlacklistDTO updateReason(Long blacklistId, String reason) {
        CustomerBlacklist blacklist = customerBlacklistRepository.findById(blacklistId)
            .orElseThrow(() -> new RuntimeException("黑名單記錄不存在"));
        
        blacklist.setReason(reason);
        blacklist = customerBlacklistRepository.save(blacklist);
        
        return convertToDTO(blacklist);
    }

    /**
     * 轉換 DTO 到 Entity
     */
    private CustomerBlacklist convertToEntity(CustomerBlacklistDTO dto) {
        CustomerBlacklist blacklist = new CustomerBlacklist();
        BeanUtils.copyProperties(dto, blacklist);
        return blacklist;
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private CustomerBlacklistDTO convertToDTO(CustomerBlacklist blacklist) {
        CustomerBlacklistDTO dto = new CustomerBlacklistDTO();
        BeanUtils.copyProperties(blacklist, dto);
        return dto;
    }
}
