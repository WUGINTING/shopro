package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.OrderItemDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderItem;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.repository.CustomerBlacklistRepository;
import com.info.ecommerce.modules.order.repository.OrderItemRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 訂單服務單元測試
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CustomerBlacklistRepository customerBlacklistRepository;

    @Mock
    private OrderHistoryService orderHistoryService;

    @InjectMocks
    private OrderService orderService;

    private OrderDTO orderDTO;
    private Order order;

    @BeforeEach
    void setUp() {
        // 準備測試資料
        orderDTO = OrderDTO.builder()
            .customerId(1L)
            .customerName("測試客戶")
            .customerPhone("0912345678")
            .customerEmail("test@example.com")
            .status(OrderStatus.PENDING_PAYMENT)
            .pickupType(PickupType.DELIVERY)
            .subtotalAmount(new BigDecimal("1000.00"))
            .discountAmount(new BigDecimal("100.00"))
            .shippingFee(new BigDecimal("60.00"))
            .totalAmount(new BigDecimal("960.00"))
            .shippingAddress("台北市信義區")
            .isDraft(false)
            .items(createTestItems())
            .build();

        order = Order.builder()
            .id(1L)
            .orderNumber("ORD20241224001")
            .customerId(1L)
            .customerName("測試客戶")
            .customerPhone("0912345678")
            .customerEmail("test@example.com")
            .status(OrderStatus.PENDING_PAYMENT)
            .pickupType(PickupType.DELIVERY)
            .subtotalAmount(new BigDecimal("1000.00"))
            .discountAmount(new BigDecimal("100.00"))
            .shippingFee(new BigDecimal("60.00"))
            .totalAmount(new BigDecimal("960.00"))
            .shippingAddress("台北市信義區")
            .isDraft(false)
            .build();
    }

    private List<OrderItemDTO> createTestItems() {
        List<OrderItemDTO> items = new ArrayList<>();
        OrderItemDTO item = OrderItemDTO.builder()
            .productId(1L)
            .productName("測試商品")
            .productSku("SKU001")
            .unitPrice(new BigDecimal("500.00"))
            .quantity(2)
            .subtotalAmount(new BigDecimal("1000.00"))
            .discountAmount(BigDecimal.ZERO)
            .actualAmount(new BigDecimal("1000.00"))
            .build();
        items.add(item);
        return items;
    }

    @Test
    void testCreateOrder_Success() {
        // 模擬黑名單檢查
        when(customerBlacklistRepository.existsByCustomerIdAndIsActive(anyLong(), anyBoolean()))
            .thenReturn(false);
        
        // 模擬訂單編號不存在
        when(orderRepository.existsByOrderNumber(any())).thenReturn(false);
        
        // 模擬保存訂單
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        // 模擬保存訂單項目
        when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>());
        
        // 模擬查詢訂單項目
        when(orderItemRepository.findByOrderId(anyLong())).thenReturn(new ArrayList<>());

        // 執行測試
        OrderDTO result = orderService.createOrder(orderDTO);

        // 驗證結果
        assertNotNull(result);
        assertEquals("測試客戶", result.getCustomerName());
        
        // 驗證方法調用
        verify(customerBlacklistRepository, times(1))
            .existsByCustomerIdAndIsActive(anyLong(), anyBoolean());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderHistoryService, times(1))
            .recordHistory(anyLong(), anyString(), anyString(), any(), anyString(), any(), any());
    }

    @Test
    void testGetOrder_Success() {
        // 模擬查詢訂單
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        // 模擬查詢訂單項目
        when(orderItemRepository.findByOrderId(1L)).thenReturn(new ArrayList<>());

        // 執行測試
        OrderDTO result = orderService.getOrder(1L);

        // 驗證結果
        assertNotNull(result);
        assertEquals("測試客戶", result.getCustomerName());
        assertEquals("ORD20241224001", result.getOrderNumber());
        
        // 驗證方法調用
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // 模擬查詢訂單
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        // 更新狀態後的訂單
        Order updatedOrder = Order.builder()
            .id(1L)
            .orderNumber("ORD20241224001")
            .customerId(1L)
            .customerName("測試客戶")
            .status(OrderStatus.PROCESSING)
            .pickupType(PickupType.DELIVERY)
            .subtotalAmount(new BigDecimal("960.00"))
            .totalAmount(new BigDecimal("960.00"))
            .isDraft(false)
            .build();
        
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(new ArrayList<>());

        // 執行測試
        OrderDTO result = orderService.updateOrderStatus(1L, OrderStatus.PROCESSING, 100L, "管理員");

        // 驗證結果
        assertNotNull(result);
        assertEquals(OrderStatus.PROCESSING, result.getStatus());
        
        // 驗證方法調用
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderHistoryService, times(1))
            .recordHistory(eq(1L), eq("UPDATE_STATUS"), anyString(), 
                eq(OrderStatus.PENDING_PAYMENT.name()), eq(OrderStatus.PROCESSING.name()), 
                eq(100L), eq("管理員"));
    }

    @Test
    void testDeleteOrder_Success() {
        // 模擬查詢訂單
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // 執行測試
        orderService.deleteOrder(1L);

        // 驗證方法調用
        verify(orderRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(1)).deleteByOrderId(1L);
        verify(orderRepository, times(1)).delete(any(Order.class));
        verify(orderHistoryService, times(1))
            .recordHistory(eq(1L), eq("DELETE"), anyString(), 
                eq(OrderStatus.PENDING_PAYMENT.name()), any(), any(), any());
    }
}
