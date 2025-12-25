package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.PaymentConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentConfigRepository extends JpaRepository<PaymentConfig, Long> {
    
    List<PaymentConfig> findByEnabledOrderBySortOrderAsc(Boolean enabled);
    
    Optional<PaymentConfig> findByProviderNameAndPaymentMethod(String providerName, String paymentMethod);
    
    Page<PaymentConfig> findByProviderNameContaining(String providerName, Pageable pageable);
    
    List<PaymentConfig> findByPaymentMethod(String paymentMethod);
}
