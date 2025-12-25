package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.system.dto.StoreDTO;
import com.info.ecommerce.modules.system.entity.Store;
import com.info.ecommerce.modules.system.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 門市管理服務
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    /**
     * 創建門市
     */
    @Transactional
    public StoreDTO createStore(StoreDTO dto) {
        // 檢查門市代碼是否已存在
        if (storeRepository.existsByStoreCode(dto.getStoreCode())) {
            throw new BusinessException("門市代碼已存在");
        }
        
        Store store = new Store();
        BeanUtils.copyProperties(dto, store, "id");
        store = storeRepository.save(store);
        return toDTO(store);
    }

    /**
     * 更新門市
     */
    @Transactional
    public StoreDTO updateStore(Long id, StoreDTO dto) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("門市不存在"));
        
        // 檢查門市代碼是否與其他門市重複
        if (storeRepository.existsByStoreCodeAndIdNot(dto.getStoreCode(), id)) {
            throw new BusinessException("門市代碼已存在");
        }
        
        BeanUtils.copyProperties(dto, store, "id", "createdAt", "updatedAt");
        store = storeRepository.save(store);
        return toDTO(store);
    }

    /**
     * 取得門市詳情
     */
    public StoreDTO getStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("門市不存在"));
        return toDTO(store);
    }

    /**
     * 依門市代碼取得門市
     */
    public StoreDTO getStoreByCode(String storeCode) {
        Store store = storeRepository.findByStoreCode(storeCode)
                .orElseThrow(() -> new BusinessException("門市不存在"));
        return toDTO(store);
    }

    /**
     * 刪除門市
     */
    @Transactional
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new BusinessException("門市不存在");
        }
        storeRepository.deleteById(id);
    }

    /**
     * 分頁查詢門市
     */
    public Page<StoreDTO> listStores(Pageable pageable) {
        return storeRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 查詢已啟用的門市
     */
    public List<StoreDTO> listEnabledStores() {
        return storeRepository.findByEnabledOrderBySortOrderAsc(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 依城市查詢門市
     */
    public Page<StoreDTO> listByCity(String city, Pageable pageable) {
        return storeRepository.findByCity(city, pageable).map(this::toDTO);
    }

    /**
     * 查詢支援取貨的門市
     */
    public List<StoreDTO> listPickupStores() {
        return storeRepository.findByAllowPickup(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 搜尋門市
     */
    public Page<StoreDTO> searchStores(String keyword, Pageable pageable) {
        return storeRepository.findByStoreNameContaining(keyword, pageable).map(this::toDTO);
    }

    /**
     * 實體轉 DTO
     */
    private StoreDTO toDTO(Store entity) {
        StoreDTO dto = new StoreDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
