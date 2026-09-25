package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.OrderHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    
    List<OrderHistory> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    
    Page<OrderHistory> findByOrderId(Long orderId, Pageable pageable);
    
    List<OrderHistory> findByOperatorId(Long operatorId);

    boolean existsByOrderIdAndActionType(Long orderId, String actionType);

    long countByOrderIdAndActionType(Long orderId, String actionType);

    List<OrderHistory> findByOrderIdAndActionTypeInOrderByCreatedAtDesc(Long orderId, java.util.Collection<String> actionTypes);

    List<OrderHistory> findByOrderIdAndActionType(Long orderId, String actionType);

    List<com.info.ecommerce.modules.order.entity.OrderHistory> findByOrderIdInAndActionType(java.util.Collection<Long> orderIds, String actionType);

    boolean existsByOrderIdAndNewStatus(Long orderId, String newStatus);

    List<OrderHistory> findByActionTypeAndCreatedAtBetween(String actionType, java.time.LocalDateTime from, java.time.LocalDateTime to);

    /** 期間內每一筆退款登記（REFUND 歷程，new_status 欄位記錄該次退款金額）；部分退款多次時逐筆計入各自的時間 */
    default List<java.math.BigDecimal> refundAmountsBetween(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        return findByActionTypeAndCreatedAtBetween("REFUND", from, to).stream()
                .map(OrderHistory::getNewStatus)
                .map(value -> {
                    try {
                        return value == null ? null : new java.math.BigDecimal(value.trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
