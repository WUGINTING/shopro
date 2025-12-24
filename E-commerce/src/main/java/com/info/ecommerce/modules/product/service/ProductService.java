package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.dto.ProductDTO;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 創建商品
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
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

    private ProductDTO toDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
