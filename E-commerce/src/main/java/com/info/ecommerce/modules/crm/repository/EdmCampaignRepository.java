package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.EdmCampaign;
import com.info.ecommerce.modules.crm.enums.EdmStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EdmCampaignRepository extends JpaRepository<EdmCampaign, Long> {
    
    Page<EdmCampaign> findByStatus(EdmStatus status, Pageable pageable);
    
    List<EdmCampaign> findByStatusAndScheduledAtBefore(EdmStatus status, LocalDateTime dateTime);
    
    Page<EdmCampaign> findByTargetGroupId(Long targetGroupId, Pageable pageable);

    /** 原子性地把活動標成發送中；已在發送、已發送或已取消時回傳 0（避免排程與手動同時寄出兩次） */
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying(flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE EdmCampaign c SET c.status = com.info.ecommerce.modules.crm.enums.EdmStatus.SENDING "
            + "WHERE c.id = :id AND c.status IN (com.info.ecommerce.modules.crm.enums.EdmStatus.DRAFT, "
            + "com.info.ecommerce.modules.crm.enums.EdmStatus.SCHEDULED, com.info.ecommerce.modules.crm.enums.EdmStatus.FAILED)")
    int claimForSending(@org.springframework.data.repository.query.Param("id") Long id);

    /** 排程發送失敗時標成失敗；只在仍是已排程狀態時更新，不會蓋掉另一個正在進行或已完成的發送 */
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE EdmCampaign c SET c.status = com.info.ecommerce.modules.crm.enums.EdmStatus.FAILED "
            + "WHERE c.id = :id AND c.status = com.info.ecommerce.modules.crm.enums.EdmStatus.SCHEDULED")
    int markFailedIfScheduled(@org.springframework.data.repository.query.Param("id") Long id);

    /** 寄送完成後寫入結果；只更新仍在發送中的活動，不覆蓋其他欄位（發送期間的編輯不會被舊資料蓋掉） */
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE EdmCampaign c SET c.status = :status, c.sentAt = :sentAt, "
            + "c.totalSent = :total, c.successCount = :success, c.failureCount = :failure "
            + "WHERE c.id = :id AND c.status = com.info.ecommerce.modules.crm.enums.EdmStatus.SENDING")
    int finishSending(@org.springframework.data.repository.query.Param("id") Long id,
                      @org.springframework.data.repository.query.Param("status") EdmStatus status,
                      @org.springframework.data.repository.query.Param("sentAt") LocalDateTime sentAt,
                      @org.springframework.data.repository.query.Param("total") Integer total,
                      @org.springframework.data.repository.query.Param("success") Integer success,
                      @org.springframework.data.repository.query.Param("failure") Integer failure);
}
