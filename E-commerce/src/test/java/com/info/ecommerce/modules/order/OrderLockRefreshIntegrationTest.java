package com.info.ecommerce.modules.order;

import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 鎖定訂單時要取得資料庫中的最新狀態：同一交易先前載入過的訂單，被其他交易改成已付款後，鎖定應看到已付款
 */
@SpringBootTest
class OrderLockRefreshIntegrationTest {

    @Autowired private OrderRepository orderRepository;
    @Autowired private PlatformTransactionManager transactionManager;

    @Test
    void lockReturnsCommittedStatus_evenIfEntityWasLoadedEarlierInTheTransaction() {
        String orderNumber = "LOCK" + UUID.randomUUID().toString().substring(0, 8);
        Long id = orderRepository.save(Order.builder()
                .orderNumber(orderNumber).customerId(0L).customerName("鎖定測試").status(OrderStatus.PENDING_PAYMENT)
                .pickupType(PickupType.DELIVERY).subtotalAmount(BigDecimal.TEN).totalAmount(BigDecimal.TEN)
                .isDraft(false).build()).getId();

        TransactionTemplate outer = new TransactionTemplate(transactionManager);
        TransactionTemplate inner = new TransactionTemplate(transactionManager);
        inner.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        OrderStatus seen = outer.execute(status -> {
            Order early = orderRepository.findByOrderNumber(orderNumber).orElseThrow();
            assertEquals(OrderStatus.PENDING_PAYMENT, early.getStatus());
            // 另一筆交易（例如綠界付款通知）先提交已付款
            inner.executeWithoutResult(s -> {
                Order paid = orderRepository.findById(id).orElseThrow();
                paid.setStatus(OrderStatus.PAID);
                orderRepository.save(paid);
            });
            return orderRepository.findByIdForUpdate(id).orElseThrow().getStatus();
        });
        assertEquals(OrderStatus.PAID, seen);
    }
}
