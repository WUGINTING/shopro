package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.enums.ProductSalesMode;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    
    Page<Product> findBySalesMode(ProductSalesMode salesMode, Pageable pageable);
    
    List<Product> findBySkuIn(List<String> skus);
    
    Page<Product> findByNameContaining(String name, Pageable pageable);

    String PUBLIC_WHERE = "WHERE p.status IN :statuses "
            + "AND (p.enabled IS NULL OR p.enabled = true) "
            + "AND (:filterCategory = false OR p.categoryId IN :categoryIds) "
            + "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "     OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))";

    String EFFECTIVE_PRICE = "CASE WHEN p.salePrice > 0 THEN p.salePrice ELSE p.basePrice END";

    /**
     * 前台商品查詢：只含指定狀態且未停用的商品，可依分類（含子分類）與關鍵字（名稱 / SKU）篩選，排序由 Pageable 指定
     */
    @Query("SELECT p FROM Product p " + PUBLIC_WHERE)
    Page<Product> findPublic(@Param("statuses") Collection<ProductStatus> statuses,
                             @Param("filterCategory") boolean filterCategory,
                             @Param("categoryIds") Collection<Long> categoryIds,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    /** 同 findPublic，依實際售價（特價優先）由低到高 */
    @Query(value = "SELECT p FROM Product p " + PUBLIC_WHERE + " ORDER BY " + EFFECTIVE_PRICE + " ASC, p.id ASC",
            countQuery = "SELECT COUNT(p) FROM Product p " + PUBLIC_WHERE)
    Page<Product> findPublicOrderByPriceAsc(@Param("statuses") Collection<ProductStatus> statuses,
                                            @Param("filterCategory") boolean filterCategory,
                                            @Param("categoryIds") Collection<Long> categoryIds,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);

    /** 同 findPublic，依實際售價（特價優先）由高到低 */
    @Query(value = "SELECT p FROM Product p " + PUBLIC_WHERE + " ORDER BY " + EFFECTIVE_PRICE + " DESC, p.id ASC",
            countQuery = "SELECT COUNT(p) FROM Product p " + PUBLIC_WHERE)
    Page<Product> findPublicOrderByPriceDesc(@Param("statuses") Collection<ProductStatus> statuses,
                                             @Param("filterCategory") boolean filterCategory,
                                             @Param("categoryIds") Collection<Long> categoryIds,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);
    
    boolean existsBySku(String sku);
    
    java.util.Optional<Product> findBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, Long id);
    
    Long countByCreatedAtBefore(LocalDateTime date);
}
