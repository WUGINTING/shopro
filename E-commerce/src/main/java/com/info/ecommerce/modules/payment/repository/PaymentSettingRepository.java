package com.info.ecommerce.modules.payment.repository;

import com.info.ecommerce.modules.payment.entity.PaymentSetting;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentSettingRepository extends JpaRepository<PaymentSetting, Long> {
    
    Optional<PaymentSetting> findByGateway(PaymentGateway gateway);
    
    List<PaymentSetting> findByEnabledTrue();
    
    List<PaymentSetting> findAllByOrderBySortOrderAsc();
}
