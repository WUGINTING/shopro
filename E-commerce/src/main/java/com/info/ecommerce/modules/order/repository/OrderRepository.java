package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByOrderNumberStartingWith(String orderNumberPrefix);

    /** 鎖定訂單列（付款回呼與逾期取消互斥，避免已付款訂單被取消） */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdBefore);
    
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    Page<Order> findByIsDraft(Boolean isDraft, Pageable pageable);
    
    Page<Order> findByStoreId(Long storeId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:customerId IS NULL OR o.customerId = :customerId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:isDraft IS NULL OR o.isDraft = :isDraft) AND " +
           "(:storeId IS NULL OR o.storeId = :storeId) AND " +
           "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR o.createdAt <= :endDate) AND " +
           "(:minAmount IS NULL OR o.totalAmount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR o.totalAmount <= :maxAmount)")
    Page<Order> findByMultipleCriteria(
        @Param("customerId") Long customerId,
        @Param("status") OrderStatus status,
        @Param("isDraft") Boolean isDraft,
        @Param("storeId") Long storeId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("minAmount") BigDecimal minAmount,
        @Param("maxAmount") BigDecimal maxAmount,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.createdAt < :date")
    Long countByStatusAndCreatedAtBefore(
        @Param("status") OrderStatus status,
        @Param("date") LocalDateTime date
    );
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    boolean existsByOrderNumber(String orderNumber);

    /** 會員在指定狀態下的訂單總金額（用於計算累計消費） */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customerId = :customerId AND o.status IN :statuses")
    BigDecimal sumTotalAmountByCustomerIdAndStatusIn(@Param("customerId") Long customerId,
                                                     @Param("statuses") java.util.Collection<OrderStatus> statuses);
}
