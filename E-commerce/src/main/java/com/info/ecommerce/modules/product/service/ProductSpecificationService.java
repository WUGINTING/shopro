package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.dto.ProductSpecificationDTO;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSpecificationService {

    private final ProductSpecificationRepository specificationRepository;
    private final ProductRepository productRepository;

    /**
     * 添加商品規格
     */
    @Transactional
    public ProductSpecificationDTO addSpecification(ProductSpecificationDTO dto) {
        // 驗證商品是否存在
        if (!productRepository.existsById(dto.getProductId())) {
            throw new BusinessException("商品不存在");
        }

        // 檢查 SKU 是否已存在
        if (dto.getSku() != null && !dto.getSku().trim().isEmpty()) {
            String normalizedSku = dto.getSku().trim();
            if (specificationRepository.findBySku(normalizedSku).isPresent()) {
                throw new BusinessException("規格編號（SKU）已存在");
            }
            dto.setSku(normalizedSku);
        }

        ProductSpecification spec = new ProductSpecification();
        BeanUtils.copyProperties(dto, spec, "id");
        spec = specificationRepository.save(spec);
        return toDTO(spec);
    }

    /**
     * 更新商品規格
     */
    @Transactional
    public ProductSpecificationDTO updateSpecification(Long id, ProductSpecificationDTO dto) {
        ProductSpecification spec = specificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("規格不存在"));

        // 檢查 SKU 是否與其他規格重複
        if (dto.getSku() != null && !dto.getSku().trim().isEmpty()) {
            String normalizedSku = dto.getSku().trim();
            String oldSku = spec.getSku() != null ? spec.getSku().trim() : null;
            
            if (oldSku == null || !normalizedSku.equals(oldSku)) {
                if (specificationRepository.findBySku(normalizedSku).isPresent()) {
                    throw new BusinessException("規格編號（SKU）已存在");
                }
            }
            dto.setSku(normalizedSku);
        }

        BeanUtils.copyProperties(dto, spec, "id", "productId", "createdAt", "updatedAt");
        spec = specificationRepository.save(spec);
        return toDTO(spec);
    }

    /**
     * 刪除商品規格
     */
    @Transactional
    public void deleteSpecification(Long id) {
        if (!specificationRepository.existsById(id)) {
            throw new BusinessException("規格不存在");
        }
        specificationRepository.deleteById(id);
    }

    /**
     * 取得商品的所有規格
     */
    public List<ProductSpecificationDTO> listProductSpecifications(Long productId) {
        return specificationRepository.findByProductId(productId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 取得規格詳情
     */
    public ProductSpecificationDTO getSpecification(Long id) {
        ProductSpecification spec = specificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("規格不存在"));
        return toDTO(spec);
    }

    /**
     * 批量創建商品規格
     */
    @Transactional
    public List<ProductSpecificationDTO> batchAddSpecifications(List<ProductSpecificationDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BusinessException("規格列表不能為空");
        }

        // 驗證商品是否存在
        Long productId = dtos.get(0).getProductId();
        if (!productRepository.existsById(productId)) {
            throw new BusinessException("商品不存在");
        }

        // 驗證所有規格是否屬於同一商品
        if (!dtos.stream().allMatch(dto -> dto.getProductId().equals(productId))) {
            throw new BusinessException("所有規格必須屬於同一商品");
        }

        // 檢查 SKU 重複
        List<String> skus = dtos.stream()
                .map(ProductSpecificationDTO::getSku)
                .filter(sku -> sku != null && !sku.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());

        for (String sku : skus) {
            if (specificationRepository.findBySku(sku).isPresent()) {
                throw new BusinessException("規格編號（SKU）" + sku + " 已存在");
            }
        }

        // 保存所有規格
        return dtos.stream().map(dto -> {
            ProductSpecification spec = new ProductSpecification();
            BeanUtils.copyProperties(dto, spec, "id");
            if (spec.getSku() != null) {
                spec.setSku(spec.getSku().trim());
            }
            spec = specificationRepository.save(spec);
            return toDTO(spec);
        }).collect(Collectors.toList());
    }

    private ProductSpecificationDTO toDTO(ProductSpecification entity) {
        ProductSpecificationDTO dto = new ProductSpecificationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
