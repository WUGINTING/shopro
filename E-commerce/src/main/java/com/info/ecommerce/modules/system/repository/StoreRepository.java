package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    Optional<Store> findByStoreCode(String storeCode);
    
    List<Store> findByEnabledOrderBySortOrderAsc(Boolean enabled);
    
    Page<Store> findByStoreNameContaining(String storeName, Pageable pageable);
    
    Page<Store> findByCity(String city, Pageable pageable);
    
    List<Store> findByAllowPickup(Boolean allowPickup);
    
    boolean existsByStoreCode(String storeCode);
    
    boolean existsByStoreCodeAndIdNot(String storeCode, Long id);
}
