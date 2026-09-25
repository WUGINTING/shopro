package com.info.ecommerce.modules.order.repository;

import com.info.ecommerce.modules.order.entity.Order;

import java.util.Optional;

public interface OrderRepositoryCustom {

    /**
     * 以悲觀鎖鎖定訂單列，並從資料庫重新讀取最新狀態。
     * 同一交易中先前已載入的訂單物件不會自動更新，因此鎖定後一定要 refresh，
     * 否則可能拿到舊狀態（例如付款通知已把訂單改成已付款，這裡仍看到待付款）。
     */
    Optional<Order> findByIdForUpdate(Long id);
}
