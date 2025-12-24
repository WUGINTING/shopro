package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.PointRecord;
import com.info.ecommerce.modules.crm.enums.PointType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {
    
    Page<PointRecord> findByMemberId(Long memberId, Pageable pageable);
    
    List<PointRecord> findByMemberIdAndPointType(Long memberId, PointType pointType);
    
    @Query("SELECT SUM(p.points) FROM PointRecord p WHERE p.memberId = :memberId")
    Integer sumPointsByMemberId(Long memberId);
}
