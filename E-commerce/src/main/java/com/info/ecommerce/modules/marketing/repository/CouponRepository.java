package com.info.ecommerce.modules.marketing.repository;

import com.info.ecommerce.modules.marketing.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    /** 前台優惠頁：公開、啟用、在效期內且尚有次數的優惠券 */
    @Query("SELECT c FROM Coupon c WHERE c.publicVisible = true AND c.enabled = true "
            + "AND c.validFrom <= :today AND c.validUntil >= :today AND c.usedCount < c.totalCount ORDER BY c.validUntil ASC")
    java.util.List<Coupon> findPublicAvailable(@Param("today") java.time.LocalDate today);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    /** 使用一次；已達總次數時不更新（回傳 0） */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Coupon c SET c.usedCount = c.usedCount + 1 WHERE c.id = :id AND c.usedCount < c.totalCount")
    int incrementUsage(@Param("id") Long id);

    /** 歸還一次使用（訂單取消） */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Coupon c SET c.usedCount = c.usedCount - 1 WHERE c.id = :id AND c.usedCount > 0")
    int decrementUsage(@Param("id") Long id);

    /** 重新使用（已取消訂單被還原時），不檢查總次數 */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Coupon c SET c.usedCount = c.usedCount + 1 WHERE c.id = :id")
    int forceIncrementUsage(@Param("id") Long id);
}
