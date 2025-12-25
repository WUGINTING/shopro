package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.ShippingConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingConfigRepository extends JpaRepository<ShippingConfig, Long> {
    
    List<ShippingConfig> findByEnabledOrderBySortOrderAsc(Boolean enabled);
    
    Optional<ShippingConfig> findByProviderNameAndShippingMethod(String providerName, String shippingMethod);
    
    Page<ShippingConfig> findByProviderNameContaining(String providerName, Pageable pageable);
    
    List<ShippingConfig> findByShippingMethod(String shippingMethod);
}
