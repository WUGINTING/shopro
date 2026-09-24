package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    
    List<ProductInventory> findByProductId(Long productId);
    
    Optional<ProductInventory> findByProductIdAndSpecificationId(Long productId, Long specificationId);
    
    List<ProductInventory> findByWarehouseId(Long warehouseId);
    
    Optional<ProductInventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    /**
     * 原子扣減商品層級（無規格）庫存，回傳受影響筆數（0 表示庫存不足或無庫存記錄）
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProductInventory i SET i.availableStock = i.availableStock - :quantity "
            + "WHERE i.productId = :productId AND i.specificationId IS NULL "
            + "AND i.availableStock IS NOT NULL AND i.availableStock >= :quantity")
    int decrementProductLevelStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProductInventory i SET i.availableStock = i.availableStock + :quantity "
            + "WHERE i.productId = :productId AND i.specificationId IS NULL AND i.availableStock IS NOT NULL")
    int incrementProductLevelStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    /**
     * 同步調整規格對應的倉儲庫存記錄（若存在），不做足量檢查（以規格庫存為準）
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProductInventory i SET i.availableStock = i.availableStock + :delta "
            + "WHERE i.productId = :productId AND i.specificationId = :specificationId AND i.availableStock IS NOT NULL")
    int adjustSpecificationStock(@Param("productId") Long productId,
                                 @Param("specificationId") Long specificationId,
                                 @Param("delta") int delta);
}
