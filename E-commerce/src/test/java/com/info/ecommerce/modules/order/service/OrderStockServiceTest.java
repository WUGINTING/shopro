package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.entity.OrderItem;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import com.info.ecommerce.modules.order.repository.OrderItemRepository;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.repository.InventoryMovementLogRepository;
import com.info.ecommerce.modules.product.repository.ProductInventoryRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStockServiceTest {

    @Mock private ProductSpecificationRepository productSpecificationRepository;
    @Mock private ProductInventoryRepository productInventoryRepository;
    @Mock private InventoryMovementLogRepository movementLogRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderHistoryRepository orderHistoryRepository;
    @Mock private OrderHistoryService orderHistoryService;
    @Mock private OrderCouponService orderCouponService;

    @InjectMocks
    private OrderStockService orderStockService;

    private static OrderItem specItem(int quantity) {
        return OrderItem.builder().orderId(1L).productId(10L).specificationId(20L).productName("茶杯").quantity(quantity).build();
    }

    @Test
    void reserve_decrementsSpecificationStockAndRecordsHistory() {
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(specItem(2)));
        when(productSpecificationRepository.decrementStock(20L, 2)).thenReturn(1);
        when(productSpecificationRepository.findById(20L))
                .thenReturn(Optional.of(ProductSpecification.builder().id(20L).stock(1).build()));

        orderStockService.reserve(1L, "ORD1");

        verify(productInventoryRepository).adjustSpecificationStock(10L, 20L, -2);
        verify(movementLogRepository).save(argThat(log -> log.getChangeQuantity() == -2
                && log.getBeforeStock() == 3 && log.getAfterStock() == 1));
        verify(orderHistoryService).recordHistory(eq(1L), eq(OrderStockService.ACTION_RESERVED), anyString(), any(), any(), any(), any());
    }

    @Test
    void reserve_insufficientStock_throws() {
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(specItem(5)));
        when(productSpecificationRepository.decrementStock(20L, 5)).thenReturn(0);
        when(productSpecificationRepository.findById(20L))
                .thenReturn(Optional.of(ProductSpecification.builder().id(20L).specName("藍色").stock(3).build()));

        BusinessException ex = assertThrows(BusinessException.class, () -> orderStockService.reserve(1L, "ORD1"));
        assertTrue(ex.getMessage().contains("剩餘 3"));
        verify(orderHistoryService, never()).recordHistory(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void reserve_untrackedStock_allowsOrder() {
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(specItem(5)));
        when(productSpecificationRepository.decrementStock(20L, 5)).thenReturn(0);
        when(productSpecificationRepository.findById(20L))
                .thenReturn(Optional.of(ProductSpecification.builder().id(20L).stock(null).build()));

        assertDoesNotThrow(() -> orderStockService.reserve(1L, "ORD1"));
    }

    @Test
    void reserve_isIdempotent() {
        when(orderHistoryRepository.countByOrderIdAndActionType(1L, OrderStockService.ACTION_RESERVED)).thenReturn(1L);

        orderStockService.reserve(1L, "ORD1");

        verifyNoInteractions(productSpecificationRepository, orderItemRepository);
    }

    @Test
    void release_onlyOnceAndOnlyWhenReserved() {
        when(orderHistoryRepository.countByOrderIdAndActionType(1L, OrderStockService.ACTION_RESERVED)).thenReturn(1L);
        when(orderHistoryRepository.countByOrderIdAndActionType(1L, OrderStockService.ACTION_RELEASED)).thenReturn(0L);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(specItem(2)));
        when(productSpecificationRepository.incrementStock(20L, 2)).thenReturn(1);
        when(productSpecificationRepository.findById(20L))
                .thenReturn(Optional.of(ProductSpecification.builder().id(20L).stock(3).build()));

        orderStockService.release(1L, "ORD1");

        verify(productSpecificationRepository).incrementStock(20L, 2);
        verify(orderHistoryService).recordHistory(eq(1L), eq(OrderStockService.ACTION_RELEASED), anyString(), any(), any(), any(), any());

        // 未扣過庫存的訂單（例如後台建立）不歸還
        when(orderHistoryRepository.countByOrderIdAndActionType(2L, OrderStockService.ACTION_RESERVED)).thenReturn(0L);
        orderStockService.release(2L, "ORD2");
        verify(orderItemRepository, never()).findByOrderId(2L);
    }

    @Test
    void reserveAgain_afterCancellation_deductsStockAgain() {
        when(orderHistoryRepository.countByOrderIdAndActionType(1L, OrderStockService.ACTION_RESERVED)).thenReturn(1L);
        when(orderHistoryRepository.countByOrderIdAndActionType(1L, OrderStockService.ACTION_RELEASED)).thenReturn(1L);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(specItem(2)));
        when(productSpecificationRepository.decrementStock(20L, 2)).thenReturn(1);
        when(productSpecificationRepository.findById(20L))
                .thenReturn(Optional.of(ProductSpecification.builder().id(20L).stock(1).build()));

        orderStockService.reserveAgain(1L, "ORD1");

        verify(productSpecificationRepository).decrementStock(20L, 2);
        verify(orderHistoryService).recordHistory(eq(1L), eq(OrderStockService.ACTION_RESERVED), anyString(), any(), any(), any(), any());
    }

    @Test
    void reserveAgain_ignoresOrdersThatNeverReservedStock() {
        when(orderHistoryRepository.countByOrderIdAndActionType(1L, OrderStockService.ACTION_RESERVED)).thenReturn(0L);

        orderStockService.reserveAgain(1L, "ORD1");

        verifyNoInteractions(orderItemRepository, productSpecificationRepository);
    }
}
