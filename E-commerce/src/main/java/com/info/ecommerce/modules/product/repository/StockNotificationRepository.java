package com.info.ecommerce.modules.product.repository;

import com.info.ecommerce.modules.product.entity.StockNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {
    
    List<StockNotification> findByProductIdAndNotifiedFalse(Long productId);
    
    List<StockNotification> findByNotifiedFalse();

    /**
     * 原子性地標記為已通知；同時處理的另一個執行緒已標記時回傳 0（避免同一位顧客收到兩封信）。
     * 以獨立交易立即提交，寄信期間不會鎖住資料列
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE StockNotification n SET n.notified = true, n.notifiedAt = :now "
            + "WHERE n.id = :id AND n.notified = false")
    int claim(@org.springframework.data.repository.query.Param("id") Long id,
              @org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);

    /** 寄信失敗時還原，下次補貨再通知 */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE StockNotification n SET n.notified = false, n.notifiedAt = null WHERE n.id = :id")
    int unclaim(@org.springframework.data.repository.query.Param("id") Long id);
}
