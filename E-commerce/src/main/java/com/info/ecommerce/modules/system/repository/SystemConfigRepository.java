package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    Optional<SystemConfig> findFirstByOrderByIdDesc();
}
