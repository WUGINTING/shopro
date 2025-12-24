package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.dto.ProductCategoryDTO;
import com.info.ecommerce.modules.product.entity.ProductCategory;
import com.info.ecommerce.modules.product.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    /**
     * 創建分類
     */
    @Transactional
    public ProductCategoryDTO createCategory(ProductCategoryDTO dto) {
        ProductCategory category = new ProductCategory();
        BeanUtils.copyProperties(dto, category, "id");
        category = categoryRepository.save(category);
        return toDTO(category);
    }

    /**
     * 更新分類
     */
    @Transactional
    public ProductCategoryDTO updateCategory(Long id, ProductCategoryDTO dto) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分類不存在"));
        
        BeanUtils.copyProperties(dto, category, "id", "createdAt", "updatedAt");
        category = categoryRepository.save(category);
        return toDTO(category);
    }

    /**
     * 取得分類詳情
     */
    public ProductCategoryDTO getCategory(Long id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分類不存在"));
        return toDTO(category);
    }

    /**
     * 刪除分類
     */
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException("分類不存在");
        }
        categoryRepository.deleteById(id);
    }

    /**
     * 取得所有分類
     */
    public List<ProductCategoryDTO> listAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 取得已啟用的分類
     */
    public List<ProductCategoryDTO> listEnabledCategories() {
        return categoryRepository.findByEnabledTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 取得頂層分類（無父分類）
     */
    public List<ProductCategoryDTO> listTopCategories() {
        return categoryRepository.findByParentIdIsNull().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 取得子分類
     */
    public List<ProductCategoryDTO> listSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProductCategoryDTO toDTO(ProductCategory entity) {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
