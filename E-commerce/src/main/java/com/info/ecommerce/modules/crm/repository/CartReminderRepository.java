package com.info.ecommerce.modules.crm.repository;

import com.info.ecommerce.modules.crm.entity.CartReminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CartReminderRepository extends JpaRepository<CartReminder, Long> {
    
    Page<CartReminder> findByMemberId(Long memberId, Pageable pageable);
    
    List<CartReminder> findByIsSentAndCreatedAtBefore(Boolean isSent, LocalDateTime dateTime);
    
    List<CartReminder> findByMemberIdAndIsSent(Long memberId, Boolean isSent);
}
