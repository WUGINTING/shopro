package com.info.ecommerce.modules.system.repository;

import com.info.ecommerce.modules.system.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    
    Page<OperationLog> findByUserId(Long userId, Pageable pageable);
    
    Page<OperationLog> findByOperationType(String operationType, Pageable pageable);
    
    Page<OperationLog> findByModule(String module, Pageable pageable);
    
    Page<OperationLog> findBySensitive(Boolean sensitive, Pageable pageable);
    
    Page<OperationLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<OperationLog> findByUserIdAndModule(Long userId, String module, Pageable pageable);
    
    List<OperationLog> findByUserIdAndOperationTypeOrderByCreatedAtDesc(Long userId, String operationType);
}
