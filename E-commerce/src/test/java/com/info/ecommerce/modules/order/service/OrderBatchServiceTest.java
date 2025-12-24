package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.BatchOrderUpdateDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderBatchService
 */
@ExtendWith(MockitoExtension.class)
class OrderBatchServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderHistoryService orderHistoryService;

    @InjectMocks
    private OrderBatchService orderBatchService;

    private Order order1;
    private Order order2;
    private BatchOrderUpdateDTO batchUpdateDTO;

    @BeforeEach
    void setUp() {
        order1 = Order.builder()
                .id(1L)
                .orderNumber("ORD001")
                .customerId(1L)
                .customerName("Customer 1")
                .status(OrderStatus.PENDING_PAYMENT)
                .pickupType(PickupType.DELIVERY)
                .totalAmount(new BigDecimal("1000.00"))
                .isDraft(false)
                .build();

        order2 = Order.builder()
                .id(2L)
                .orderNumber("ORD002")
                .customerId(2L)
                .customerName("Customer 2")
                .status(OrderStatus.PROCESSING)
                .pickupType(PickupType.DELIVERY)
                .totalAmount(new BigDecimal("2000.00"))
                .isDraft(false)
                .build();

        batchUpdateDTO = BatchOrderUpdateDTO.builder()
                .orderIds(List.of(1L, 2L))
                .targetStatus(OrderStatus.COMPLETED)
                .operatorId(100L)
                .operatorName("Admin")
                .notes("Batch update test")
                .build();
    }

    @Test
    void should_UpdateAllOrders_When_BatchUpdateStatusWithValidOrders() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order2));
        when(orderRepository.save(any(Order.class))).thenReturn(order1, order2);

        // when
        List<Long> result = orderBatchService.batchUpdateStatus(batchUpdateDTO);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(1L, 2L);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findById(2L);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderHistoryService, times(2)).recordHistory(
                anyLong(), eq("BATCH_UPDATE_STATUS"), anyString(),
                anyString(), eq(OrderStatus.COMPLETED.name()),
                eq(100L), eq("Admin")
        );
    }

    @Test
    void should_ThrowBusinessException_When_BatchUpdateStatusWithNonExistentOrder() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderBatchService.batchUpdateStatus(batchUpdateDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("部分訂單更新失敗");
        
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findById(2L);
    }

    @Test
    void should_SetCompletedAt_When_BatchUpdateStatusToCompleted() {
        // given
        BatchOrderUpdateDTO singleUpdateDTO = BatchOrderUpdateDTO.builder()
                .orderIds(List.of(1L))
                .targetStatus(OrderStatus.COMPLETED)
                .operatorId(100L)
                .operatorName("Admin")
                .notes("Test completion")
                .build();
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            assertThat(order.getCompletedAt()).isNotNull();
            return order;
        });

        // when
        List<Long> result = orderBatchService.batchUpdateStatus(singleUpdateDTO);

        // then
        assertThat(result).hasSize(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void should_RecordHistoryWithoutNotes_When_NotesNotProvided() {
        // given
        batchUpdateDTO.setNotes(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order2));
        when(orderRepository.save(any(Order.class))).thenReturn(order1, order2);

        // when
        List<Long> result = orderBatchService.batchUpdateStatus(batchUpdateDTO);

        // then
        assertThat(result).hasSize(2);
        verify(orderHistoryService, times(2)).recordHistory(
                anyLong(), eq("BATCH_UPDATE_STATUS"), 
                eq("批次更新訂單狀態: "),
                anyString(), eq(OrderStatus.COMPLETED.name()),
                eq(100L), eq("Admin")
        );
    }

    @Test
    void should_DeleteAllOrders_When_BatchDeleteOrdersWithValidIds() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order2));

        // when
        orderBatchService.batchDeleteOrders(List.of(1L, 2L));

        // then
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findById(2L);
        verify(orderRepository, times(1)).delete(order1);
        verify(orderRepository, times(1)).delete(order2);
        verify(orderHistoryService, times(2)).recordHistory(
                anyLong(), eq("BATCH_DELETE"), eq("批次刪除訂單"),
                anyString(), isNull(), isNull(), isNull()
        );
    }

    @Test
    void should_NotDeleteOrders_When_BatchDeleteOrdersWithNonExistentIds() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());

        // when
        orderBatchService.batchDeleteOrders(List.of(1L, 2L));

        // then
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findById(2L);
        verify(orderRepository, never()).delete(any(Order.class));
        verify(orderHistoryService, never()).recordHistory(
                anyLong(), anyString(), anyString(),
                anyString(), anyString(), anyLong(), anyString()
        );
    }

    @Test
    void should_ExportAllOrders_When_OrderIdsIsNull() {
        // given
        List<Order> allOrders = List.of(order1, order2);
        when(orderRepository.findAll()).thenReturn(allOrders);

        // when
        List<Order> result = orderBatchService.exportOrders(null);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(order1, order2);
        verify(orderRepository, times(1)).findAll();
        verify(orderRepository, never()).findAllById(anyList());
    }

    @Test
    void should_ExportAllOrders_When_OrderIdsIsEmpty() {
        // given
        List<Order> allOrders = List.of(order1, order2);
        when(orderRepository.findAll()).thenReturn(allOrders);

        // when
        List<Order> result = orderBatchService.exportOrders(new ArrayList<>());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(order1, order2);
        verify(orderRepository, times(1)).findAll();
        verify(orderRepository, never()).findAllById(anyList());
    }

    @Test
    void should_ExportSpecificOrders_When_OrderIdsProvided() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        List<Order> orders = List.of(order1, order2);
        when(orderRepository.findAllById(orderIds)).thenReturn(orders);

        // when
        List<Order> result = orderBatchService.exportOrders(orderIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(order1, order2);
        verify(orderRepository, times(1)).findAllById(orderIds);
        verify(orderRepository, never()).findAll();
    }
}
