package com.info.ecommerce.modules.payment.repository;

import com.info.ecommerce.modules.payment.entity.EcPayConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ECPay 配置 Repository
 */
@Repository
public interface EcPayConfigRepository extends JpaRepository<EcPayConfigEntity, Long> {
    
    /**
     * 查找啟用的配置（應該只有一筆）
     */
    Optional<EcPayConfigEntity> findByEnabledTrue();
    
    /**
     * 查找第一筆配置
     */
    Optional<EcPayConfigEntity> findFirstByOrderByIdAsc();
}

