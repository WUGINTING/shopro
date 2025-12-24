package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.entity.ProductTag;
import com.info.ecommerce.modules.product.entity.ProductTagRelation;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductTagRelationRepository;
import com.info.ecommerce.modules.product.repository.ProductTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTagService {

    private final ProductTagRepository productTagRepository;
    private final ProductTagRelationRepository productTagRelationRepository;
    private final ProductRepository productRepository;

    /**
     * 創建標籤
     */
    @Transactional
    public ProductTag createTag(ProductTag tag) {
        // 檢查標籤名稱是否已存在
        if (productTagRepository.findByName(tag.getName()).isPresent()) {
            throw new BusinessException("標籤名稱已存在");
        }
        return productTagRepository.save(tag);
    }

    /**
     * 更新標籤
     */
    @Transactional
    public ProductTag updateTag(Long id, ProductTag tag) {
        ProductTag existingTag = productTagRepository.findById(id)
                .orElseThrow(() -> new BusinessException("標籤不存在"));

        // 檢查新標籤名稱是否與其他標籤重複
        if (!existingTag.getName().equals(tag.getName())) {
            if (productTagRepository.findByName(tag.getName()).isPresent()) {
                throw new BusinessException("標籤名稱已存在");
            }
        }

        existingTag.setName(tag.getName());
        existingTag.setColor(tag.getColor());
        existingTag.setDescription(tag.getDescription());
        return productTagRepository.save(existingTag);
    }

    /**
     * 刪除標籤
     */
    @Transactional
    public void deleteTag(Long id) {
        if (!productTagRepository.existsById(id)) {
            throw new BusinessException("標籤不存在");
        }
        // 刪除標籤的同時刪除所有關聯關係
        productTagRelationRepository.deleteByTagId(id);
        productTagRepository.deleteById(id);
    }

    /**
     * 取得所有標籤
     */
    public List<ProductTag> listAllTags() {
        return productTagRepository.findAll();
    }

    /**
     * 為商品添加標籤
     */
    @Transactional
    public void addTagToProduct(Long productId, Long tagId) {
        // 驗證商品和標籤是否存在
        if (!productRepository.existsById(productId)) {
            throw new BusinessException("商品不存在");
        }
        if (!productTagRepository.existsById(tagId)) {
            throw new BusinessException("標籤不存在");
        }

        // 檢查關聯是否已存在
        if (productTagRelationRepository.findByProductIdAndTagId(productId, tagId).isPresent()) {
            throw new BusinessException("該標籤已添加到此商品");
        }

        ProductTagRelation relation = ProductTagRelation.builder()
                .productId(productId)
                .tagId(tagId)
                .build();
        productTagRelationRepository.save(relation);
    }

    /**
     * 移除商品標籤
     */
    @Transactional
    public void removeTagFromProduct(Long productId, Long tagId) {
        ProductTagRelation relation = productTagRelationRepository
                .findByProductIdAndTagId(productId, tagId)
                .orElseThrow(() -> new BusinessException("標籤關聯不存在"));
        productTagRelationRepository.delete(relation);
    }

    /**
     * 取得商品的所有標籤
     */
    public List<ProductTag> listProductTags(Long productId) {
        List<Long> tagIds = productTagRelationRepository.findByProductId(productId).stream()
                .map(ProductTagRelation::getTagId)
                .collect(Collectors.toList());
        
        if (tagIds.isEmpty()) {
            return List.of();
        }
        
        return productTagRepository.findAllById(tagIds);
    }

    /**
     * 批量設置商品標籤（替換現有標籤）
     */
    @Transactional
    public void setProductTags(Long productId, List<Long> tagIds) {
        // 驗證商品是否存在
        if (!productRepository.existsById(productId)) {
            throw new BusinessException("商品不存在");
        }

        // 刪除現有標籤
        productTagRelationRepository.deleteByProductId(productId);

        // 添加新標籤
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                if (!productTagRepository.existsById(tagId)) {
                    throw new BusinessException("標籤 ID " + tagId + " 不存在");
                }
                ProductTagRelation relation = ProductTagRelation.builder()
                        .productId(productId)
                        .tagId(tagId)
                        .build();
                productTagRelationRepository.save(relation);
            }
        }
    }
}
