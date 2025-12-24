package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.dto.WarehouseDTO;
import com.info.ecommerce.modules.product.entity.Warehouse;
import com.info.ecommerce.modules.product.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 倉庫管理服務
 */
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    /**
     * 創建倉庫
     */
    @Transactional
    public WarehouseDTO createWarehouse(WarehouseDTO dto) {
        Warehouse warehouse = new Warehouse();
        BeanUtils.copyProperties(dto, warehouse, "id");
        
        // 如果設為預設倉庫，需要取消其他倉庫的預設狀態
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefaultWarehouse();
        }
        
        warehouse = warehouseRepository.save(warehouse);
        return toDTO(warehouse);
    }

    /**
     * 更新倉庫
     */
    @Transactional
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("倉庫不存在"));
        
        // 如果設為預設倉庫，需要取消其他倉庫的預設狀態
        if (Boolean.TRUE.equals(dto.getIsDefault()) && !Boolean.TRUE.equals(warehouse.getIsDefault())) {
            clearDefaultWarehouse();
        }
        
        BeanUtils.copyProperties(dto, warehouse, "id", "createdAt", "updatedAt");
        warehouse = warehouseRepository.save(warehouse);
        return toDTO(warehouse);
    }

    /**
     * 取得倉庫詳情
     */
    public WarehouseDTO getWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("倉庫不存在"));
        return toDTO(warehouse);
    }

    /**
     * 刪除倉庫
     */
    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("倉庫不存在"));
        
        if (Boolean.TRUE.equals(warehouse.getIsDefault())) {
            throw new BusinessException("無法刪除預設倉庫");
        }
        
        warehouseRepository.deleteById(id);
    }

    /**
     * 取得所有倉庫
     */
    public List<WarehouseDTO> listAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 取得已啟用的倉庫
     */
    public List<WarehouseDTO> listEnabledWarehouses() {
        return warehouseRepository.findByEnabledTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 取得預設倉庫
     */
    public WarehouseDTO getDefaultWarehouse() {
        return warehouseRepository.findByIsDefaultTrue()
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * 清除所有倉庫的預設狀態
     */
    private void clearDefaultWarehouse() {
        warehouseRepository.findByIsDefaultTrue().ifPresent(warehouse -> {
            warehouse.setIsDefault(false);
            warehouseRepository.save(warehouse);
        });
    }

    private WarehouseDTO toDTO(Warehouse entity) {
        WarehouseDTO dto = new WarehouseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
