package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.album.entity.AlbumImage;
import com.info.ecommerce.modules.album.repository.AlbumImageRepository;
import com.info.ecommerce.modules.product.dto.ProductDTO;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductCategoryRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final AlbumImageRepository albumImageRepository;

    /**
     * 驗證並標準化 SKU
     * @return 標準化後的 SKU，如果輸入為 null 或空白則返回 null
     */
    private String validateAndNormalizeSku(String sku) {
        if (sku != null && !sku.trim().isEmpty()) {
            return sku.trim();
        }
        return null;
    }

    /**
     * 驗證分類是否存在
     */
    private void validateCategory(Long categoryId) {
        if (categoryId != null && !productCategoryRepository.existsById(categoryId)) {
            throw new BusinessException("商品分類不存在");
        }
    }

    /**
     * 創建商品
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        // 檢查分類是否存在
        validateCategory(dto.getCategoryId());
        
        // 檢查 SKU 是否已存在
        String normalizedSku = validateAndNormalizeSku(dto.getSku());
        if (normalizedSku != null) {
            if (productRepository.existsBySku(normalizedSku)) {
                throw new BusinessException("商品編號（SKU）已存在，請使用其他編號");
            }
            dto.setSku(normalizedSku);
        }
        
        Product product = new Product();
        BeanUtils.copyProperties(dto, product, "id");
        product = productRepository.save(product);
        return toDTO(product);
    }

    /**
     * 更新商品
     */
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        
        // 檢查分類是否存在
        validateCategory(dto.getCategoryId());
        
        // 檢查 SKU 是否與其他商品重複
        String normalizedSku = validateAndNormalizeSku(dto.getSku());
        if (normalizedSku != null) {
            // 檢查新的 SKU 是否已被其他商品使用（排除當前商品本身）
            if (productRepository.existsBySkuAndIdNot(normalizedSku, id)) {
                throw new BusinessException("商品編號（SKU）已存在，請使用其他編號");
            }
            dto.setSku(normalizedSku);
        }
        
        BeanUtils.copyProperties(dto, product, "id", "createdAt", "updatedAt");
        product = productRepository.save(product);
        return toDTO(product);
    }

    /**
     * 取得商品詳情
     */
    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        return toDTO(product);
    }

    /**
     * 刪除商品
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException("商品不存在");
        }
        productRepository.deleteById(id);
    }

    /**
     * 分頁查詢商品
     */
    public Page<ProductDTO> listProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * 依分類查詢商品
     */
    public Page<ProductDTO> listProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).map(this::toDTO);
    }

    /**
     * 依狀態查詢商品
     */
    public Page<ProductDTO> listProductsByStatus(ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    /**
     * 搜尋商品
     */
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable).map(this::toDTO);
    }

    /**
     * 上架商品
     */
    @Transactional
    public ProductDTO activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        product.setStatus(ProductStatus.ACTIVE);
        product = productRepository.save(product);
        return toDTO(product);
    }

    /**
     * 下架商品
     */
    @Transactional
    public ProductDTO deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        product.setStatus(ProductStatus.INACTIVE);
        product = productRepository.save(product);
        return toDTO(product);
    }

    /**
     * 從相冊添加圖片到商品
     */
    @Transactional
    public ProductDTO addAlbumImagesToProduct(Long productId, List<Long> albumImageIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("商品不存在"));

        // 獲取當前商品的圖片列表
        List<String> imageUrls = product.getImageUrls();
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }

        // 批次獲取所有相冊圖片（避免 N+1 查詢問題）
        List<AlbumImage> albumImages = albumImageRepository.findAllById(albumImageIds);
        
        // 驗證所有圖片都存在
        if (albumImages.size() != albumImageIds.size()) {
            throw new BusinessException("部分相冊圖片不存在");
        }

        // 添加相冊圖片的 URL
        for (AlbumImage albumImage : albumImages) {
            // 添加圖片 URL（避免重複）
            if (!imageUrls.contains(albumImage.getImageUrl())) {
                imageUrls.add(albumImage.getImageUrl());
            }
        }

        product.setImageUrls(imageUrls);
        product = productRepository.save(product);
        return toDTO(product);
    }

    private ProductDTO toDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
