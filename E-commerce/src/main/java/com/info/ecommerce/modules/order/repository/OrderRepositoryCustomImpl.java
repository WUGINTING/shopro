package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Order> findByIdForUpdate(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Order order = entityManager.find(Order.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (order == null) {
            return Optional.empty();
        }
        // 已在持久化內容中的物件，find 不會重新讀取欄位；refresh 取得鎖定後的最新資料
        entityManager.refresh(order, LockModeType.PESSIMISTIC_WRITE);
        return Optional.of(order);
    }
}
