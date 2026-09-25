package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    void deleteByOrderId(Long orderId);

    /** 指定期間、指定訂單狀態的商品銷售數量排行：[productId, 數量] */
    @org.springframework.data.jpa.repository.Query("SELECT i.productId, SUM(i.quantity) FROM OrderItem i, Order o "
            + "WHERE i.orderId = o.id AND o.status IN :statuses AND o.createdAt >= :since "
            + "GROUP BY i.productId ORDER BY SUM(i.quantity) DESC")
    List<Object[]> findTopSellingProducts(@org.springframework.data.repository.query.Param("statuses") java.util.Collection<com.info.ecommerce.modules.order.enums.OrderStatus> statuses,
                                          @org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since,
                                          org.springframework.data.domain.Pageable pageable);

    List<OrderItem> findByOrderIdIn(java.util.Collection<Long> orderIds);
}
