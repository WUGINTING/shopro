package com.info.ecommerce.modules.store.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.store.dto.StoreDTO;
import com.info.ecommerce.modules.store.entity.Store;
import com.info.ecommerce.modules.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    /**
     * 取得商店設定（單店模式只有一筆）
     */
    public StoreDTO getStore() {
        Store store = storeRepository.findById(1L)
                .orElseThrow(() -> new BusinessException("商店設定不存在"));
        return toDTO(store);
    }
    /**
     * 初始化商店設定
     */
    @Transactional
    public StoreDTO createStore(StoreDTO dto) {
        // 1. 檢查是否已有資料 (單店模式禁止第二筆)
        if (storeRepository.count() > 0) {
            throw new BusinessException("商店設定已存在");
        }

        Store store = new Store();
        // 2. 不要手動 setId(1L)，讓資料庫自己產生
        BeanUtils.copyProperties(dto, store, "id");

        store = storeRepository.save(store); // 儲存後 id 會自動變成 1
        return toDTO(store);
    }
    /**
     * 初始化或更新商店設定
     */
    @Transactional
    public StoreDTO saveStore(StoreDTO dto) {
        // 3. 更新時，必須先從 DB 查出受管轄的實體
        Store store = storeRepository.findById(1L)
                .orElseThrow(() -> new BusinessException("商店未初始化"));

        BeanUtils.copyProperties(dto, store, "id", "createdAt", "updatedAt");
        return toDTO(storeRepository.save(store));
    }

    private StoreDTO toDTO(Store entity) {
        StoreDTO dto = new StoreDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
