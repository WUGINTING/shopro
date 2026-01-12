package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderPayment;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PaymentStatus;
import com.info.ecommerce.modules.order.repository.OrderPaymentRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.order.service.OrderHistoryService;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 支付回調服務測試
 */
@ExtendWith(MockitoExtension.class)
class PaymentCallbackServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPaymentRepository orderPaymentRepository;

    @Mock
    private OrderHistoryService orderHistoryService;

    @InjectMocks
    private PaymentCallbackService paymentCallbackService;

    private Order testOrder;
    private PaymentResponseDTO successResponse;
    private PaymentResponseDTO failureResponse;

    @BeforeEach
    void setUp() {
        // 準備測試訂單
        testOrder = Order.builder()
                .id(1L)
                .orderNumber("TEST20240101001")
                .customerId(1L)
                .customerName("測試客戶")
                .status(OrderStatus.PENDING_PAYMENT)
                .totalAmount(new BigDecimal("1000"))
                .build();

        // 準備成功的支付回應
        successResponse = PaymentResponseDTO.builder()
                .gateway(PaymentGateway.ECPAY)
                .status(PaymentGatewayStatus.SUCCESS)
                .orderNumber("TEST20240101001")
                .transactionId("ECPAY2024010112345")
                .amount(new BigDecimal("1000"))
                .build();

        // 準備失敗的支付回應
        failureResponse = PaymentResponseDTO.builder()
                .gateway(PaymentGateway.ECPAY)
                .status(PaymentGatewayStatus.FAILED)
                .orderNumber("TEST20240101001")
                .transactionId("ECPAY2024010112346")
                .amount(new BigDecimal("1000"))
                .errorMessage("信用卡授權失敗")
                .build();
    }

    @Test
    void testHandlePaymentSuccess_ValidOrder() {
        // Arrange
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.of(testOrder));
        when(orderPaymentRepository.findByOrderIdAndGatewayTransactionId(anyLong(), anyString()))
                .thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderPaymentRepository.save(any(OrderPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = paymentCallbackService.handlePaymentSuccess(successResponse);

        // Assert
        assertTrue(result);
        verify(orderRepository).findByOrderNumber("TEST20240101001");
        verify(orderRepository).save(argThat(order -> 
                order.getStatus() == OrderStatus.PAID && order.getPaymentTime() != null
        ));
        verify(orderPaymentRepository).save(argThat(payment ->
                payment.getPaymentStatus() == PaymentStatus.PAID &&
                payment.getGatewayTransactionId().equals("ECPAY2024010112345")
        ));
        verify(orderHistoryService).recordHistory(
                eq(1L),
                eq("PAYMENT_SUCCESS"),
                anyString(),
                eq(OrderStatus.PENDING_PAYMENT.name()),
                eq(OrderStatus.PAID.name()),
                isNull(),
                isNull()
        );
    }

    @Test
    void testHandlePaymentSuccess_OrderNotFound() {
        // Arrange
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.empty());

        // Act
        boolean result = paymentCallbackService.handlePaymentSuccess(successResponse);

        // Assert
        assertFalse(result);
        verify(orderRepository).findByOrderNumber("TEST20240101001");
        verify(orderRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    void testHandlePaymentSuccess_OrderAlreadyPaid() {
        // Arrange
        testOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.of(testOrder));

        // Act
        boolean result = paymentCallbackService.handlePaymentSuccess(successResponse);

        // Assert
        assertFalse(result);
        verify(orderRepository).findByOrderNumber("TEST20240101001");
        verify(orderRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    void testHandlePaymentSuccess_UpdateExistingPayment() {
        // Arrange
        OrderPayment existingPayment = OrderPayment.builder()
                .id(1L)
                .orderId(1L)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.of(testOrder));
        when(orderPaymentRepository.findByOrderIdAndGatewayTransactionId(1L, "ECPAY2024010112345"))
                .thenReturn(Optional.of(existingPayment));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderPaymentRepository.save(any(OrderPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = paymentCallbackService.handlePaymentSuccess(successResponse);

        // Assert
        assertTrue(result);
        verify(orderPaymentRepository).save(argThat(payment ->
                payment.getId() == 1L &&
                payment.getPaymentStatus() == PaymentStatus.PAID
        ));
    }

    @Test
    void testHandlePaymentFailure_ValidOrder() {
        // Arrange
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.of(testOrder));
        when(orderPaymentRepository.save(any(OrderPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = paymentCallbackService.handlePaymentFailure(failureResponse);

        // Assert
        assertTrue(result);
        verify(orderRepository).findByOrderNumber("TEST20240101001");
        verify(orderPaymentRepository).save(argThat(payment ->
                payment.getPaymentStatus() == PaymentStatus.FAILED &&
                payment.getNotes().contains("信用卡授權失敗")
        ));
        verify(orderHistoryService).recordHistory(
                eq(1L),
                eq("PAYMENT_FAILED"),
                anyString(),
                anyString(),
                anyString(),
                isNull(),
                isNull()
        );
    }

    @Test
    void testHandlePaymentFailure_OrderNotFound() {
        // Arrange
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.empty());

        // Act
        boolean result = paymentCallbackService.handlePaymentFailure(failureResponse);

        // Assert
        assertFalse(result);
        verify(orderRepository).findByOrderNumber("TEST20240101001");
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    void testHandlePaymentCancellation_ValidOrder() {
        // Arrange
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        boolean result = paymentCallbackService.handlePaymentCancellation("TEST20240101001");

        // Assert
        assertTrue(result);
        verify(orderRepository).findByOrderNumber("TEST20240101001");
        verify(orderRepository).save(argThat(order ->
                order.getStatus() == OrderStatus.CANCELLED
        ));
        verify(orderHistoryService).recordHistory(
                eq(1L),
                eq("PAYMENT_CANCELLED"),
                eq("支付已取消"),
                eq(OrderStatus.PENDING_PAYMENT.name()),
                eq(OrderStatus.CANCELLED.name()),
                isNull(),
                isNull()
        );
    }

    @Test
    void testHandlePaymentCancellation_OrderNotFound() {
        // Arrange
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.empty());

        // Act
        boolean result = paymentCallbackService.handlePaymentCancellation("TEST20240101001");

        // Assert
        assertFalse(result);
        verify(orderRepository).findByOrderNumber("TEST20240101001");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testHandlePaymentCancellation_OrderAlreadyCompleted() {
        // Arrange
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.of(testOrder));

        // Act
        boolean result = paymentCallbackService.handlePaymentCancellation("TEST20240101001");

        // Assert
        assertFalse(result);
        verify(orderRepository).findByOrderNumber("TEST20240101001");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testHandlePaymentCancellation_ProcessingOrder() {
        // Arrange
        testOrder.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findByOrderNumber("TEST20240101001"))
                .thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        boolean result = paymentCallbackService.handlePaymentCancellation("TEST20240101001");

        // Assert
        assertTrue(result);
        verify(orderRepository).save(argThat(order ->
                order.getStatus() == OrderStatus.CANCELLED
        ));
    }
}
