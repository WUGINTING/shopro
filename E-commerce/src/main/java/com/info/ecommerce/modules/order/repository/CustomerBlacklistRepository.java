package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.CustomerBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerBlacklistRepository extends JpaRepository<CustomerBlacklist, Long> {
    
    Optional<CustomerBlacklist> findByCustomerIdAndIsActive(Long customerId, Boolean isActive);
    
    List<CustomerBlacklist> findByIsActive(Boolean isActive);
    
    boolean existsByCustomerIdAndIsActive(Long customerId, Boolean isActive);
}
