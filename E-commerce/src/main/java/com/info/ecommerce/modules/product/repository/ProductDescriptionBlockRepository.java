package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.ProductDescriptionBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDescriptionBlockRepository extends JpaRepository<ProductDescriptionBlock, Long> {

    /**
     * 根據商品ID查找所有描述區塊
     */
    List<ProductDescriptionBlock> findByProductIdOrderByBlockOrderAsc(Long productId);

    /**
     * 根據商品ID和區塊類型查找描述區塊
     */
    List<ProductDescriptionBlock> findByProductIdAndBlockTypeOrderByBlockOrderAsc(Long productId, String blockType);

    /**
     * 根據商品ID、區塊類型和區塊編號查找描述區塊
     */
    Optional<ProductDescriptionBlock> findByProductIdAndBlockTypeAndBlockNumber(Long productId, String blockType, Integer blockNumber);

    /**
     * 刪除商品的所有描述區塊
     */
    void deleteByProductId(Long productId);

    /**
     * 檢查是否存在指定商品、類型、編號的區塊
     */
    boolean existsByProductIdAndBlockTypeAndBlockNumber(Long productId, String blockType, Integer blockNumber);
}

