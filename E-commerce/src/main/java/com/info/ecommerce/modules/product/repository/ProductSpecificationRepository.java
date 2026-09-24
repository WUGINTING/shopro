package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {
    
    List<ProductSpecification> findByProductId(Long productId);
    
    Optional<ProductSpecification> findBySku(String sku);
    
    List<ProductSpecification> findByProductIdAndEnabledTrue(Long productId);

    /**
     * 原子扣減規格庫存：只有庫存足夠時才扣，回傳受影響筆數（0 表示庫存不足或未追蹤庫存）
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProductSpecification s SET s.stock = s.stock - :quantity "
            + "WHERE s.id = :id AND s.stock IS NOT NULL AND s.stock >= :quantity")
    int decrementStock(@Param("id") Long id, @Param("quantity") int quantity);

    /**
     * 歸還規格庫存（訂單取消時）
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProductSpecification s SET s.stock = s.stock + :quantity WHERE s.id = :id AND s.stock IS NOT NULL")
    int incrementStock(@Param("id") Long id, @Param("quantity") int quantity);
}
