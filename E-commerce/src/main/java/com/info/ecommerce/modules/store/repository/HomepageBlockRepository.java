package com.info.ecommerce.modules.store.repository;

import com.info.ecommerce.modules.store.entity.HomepageBlock;
import com.info.ecommerce.modules.store.enums.BlockType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomepageBlockRepository extends JpaRepository<HomepageBlock, Long> {

    List<HomepageBlock> findByEnabledTrueOrderBySortOrderAsc();

    List<HomepageBlock> findAllByOrderBySortOrderAsc();

    List<HomepageBlock> findByBlockType(BlockType blockType);

    long countByEnabledTrue();
}
