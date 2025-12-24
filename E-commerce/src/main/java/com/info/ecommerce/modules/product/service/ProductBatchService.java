package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.dto.BatchUpdateProductDTO;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 批次操作服務
 * 支持批次更新、批次刪除等功能
 */
@Service
@RequiredArgsConstructor
public class ProductBatchService {

    private final ProductRepository productRepository;

    /**
     * 批次更新商品
     */
    @Transactional
    public void batchUpdateProducts(BatchUpdateProductDTO dto) {
        List<Product> products = productRepository.findAllById(dto.getProductIds());
        
        if (products.size() != dto.getProductIds().size()) {
            throw new BusinessException("部分商品不存在");
        }

        for (Product product : products) {
            if (dto.getStatus() != null) {
                product.setStatus(dto.getStatus());
            }
            if (dto.getCategoryId() != null) {
                product.setCategoryId(dto.getCategoryId());
            }
            if (dto.getSalePrice() != null) {
                product.setSalePrice(dto.getSalePrice());
            }
            if (dto.getSortOrder() != null) {
                product.setSortOrder(dto.getSortOrder());
            }
            if (dto.getEnabled() != null) {
                product.setEnabled(dto.getEnabled());
            }
        }

        productRepository.saveAll(products);
    }

    /**
     * 批次刪除商品
     */
    @Transactional
    public void batchDeleteProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new BusinessException("商品 ID 列表不可為空");
        }

        List<Product> products = productRepository.findAllById(productIds);
        
        if (products.size() != productIds.size()) {
            throw new BusinessException("部分商品不存在");
        }

        productRepository.deleteAll(products);
    }

    /**
     * 批次上架商品
     */
    @Transactional
    public void batchActivateProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new BusinessException("商品 ID 列表不可為空");
        }

        List<Product> products = productRepository.findAllById(productIds);
        products.forEach(product -> product.setStatus(com.info.ecommerce.modules.product.enums.ProductStatus.ACTIVE));
        productRepository.saveAll(products);
    }

    /**
     * 批次下架商品
     */
    @Transactional
    public void batchDeactivateProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new BusinessException("商品 ID 列表不可為空");
        }

        List<Product> products = productRepository.findAllById(productIds);
        products.forEach(product -> product.setStatus(com.info.ecommerce.modules.product.enums.ProductStatus.INACTIVE));
        productRepository.saveAll(products);
    }
}
