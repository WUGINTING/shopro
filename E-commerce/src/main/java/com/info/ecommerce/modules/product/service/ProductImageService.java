package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.dto.ProductImageDTO;
import com.info.ecommerce.modules.product.entity.ProductImage;
import com.info.ecommerce.modules.product.repository.ProductImageRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    /**
     * 添加商品圖片
     */
    @Transactional
    public ProductImageDTO addProductImage(ProductImageDTO dto) {
        // 驗證商品是否存在
        if (!productRepository.existsById(dto.getProductId())) {
            throw new BusinessException("商品不存在");
        }

        // 如果設置為主圖，先將該商品的其他主圖取消
        if (dto.getIsPrimary() != null && dto.getIsPrimary()) {
            productImageRepository.findByProductId(dto.getProductId())
                    .forEach(image -> {
                        image.setIsPrimary(false);
                        productImageRepository.save(image);
                    });
        }

        ProductImage image = new ProductImage();
        BeanUtils.copyProperties(dto, image, "id");
        image = productImageRepository.save(image);
        return toDTO(image);
    }

    /**
     * 更新商品圖片
     */
    @Transactional
    public ProductImageDTO updateProductImage(Long id, ProductImageDTO dto) {
        ProductImage image = productImageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("圖片不存在"));

        // 如果設置為主圖，先將該商品的其他主圖取消
        if (dto.getIsPrimary() != null && dto.getIsPrimary() && !image.getIsPrimary()) {
            productImageRepository.findByProductId(image.getProductId())
                    .forEach(img -> {
                        if (!img.getId().equals(id)) {
                            img.setIsPrimary(false);
                            productImageRepository.save(img);
                        }
                    });
        }

        BeanUtils.copyProperties(dto, image, "id", "productId", "createdAt");
        image = productImageRepository.save(image);
        return toDTO(image);
    }

    /**
     * 刪除商品圖片
     */
    @Transactional
    public void deleteProductImage(Long id) {
        if (!productImageRepository.existsById(id)) {
            throw new BusinessException("圖片不存在");
        }
        productImageRepository.deleteById(id);
    }

    /**
     * 取得商品的所有圖片
     */
    public List<ProductImageDTO> listProductImages(Long productId) {
        return productImageRepository.findByProductId(productId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 設置主圖
     */
    @Transactional
    public ProductImageDTO setPrimaryImage(Long id) {
        ProductImage image = productImageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("圖片不存在"));

        // 先將該商品的其他主圖取消
        productImageRepository.findByProductId(image.getProductId())
                .forEach(img -> {
                    img.setIsPrimary(false);
                    productImageRepository.save(img);
                });

        // 設置當前圖片為主圖
        image.setIsPrimary(true);
        image = productImageRepository.save(image);
        return toDTO(image);
    }

    private ProductImageDTO toDTO(ProductImage entity) {
        ProductImageDTO dto = new ProductImageDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
