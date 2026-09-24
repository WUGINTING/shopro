package com.info.ecommerce.modules.order.event;

import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.OrderItemDTO;
import com.info.ecommerce.modules.order.enums.NotificationType;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.service.OrderNotificationService;
import com.info.ecommerce.modules.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEmailListenerTest {

    @Mock private OrderService orderService;
    @Mock private OrderNotificationService orderNotificationService;

    @InjectMocks
    private OrderEmailListener listener;

    private OrderDTO order;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "enabled", true);
        ReflectionTestUtils.setField(listener, "storeName", "遇日小舖");
        ReflectionTestUtils.setField(listener, "storefrontUrl", "https://shop.example.com/");
        order = OrderDTO.builder()
                .id(7L).orderNumber("ORD2026092400000001")
                .customerName("王小明").customerEmail("buyer@example.com")
                .pickupType(PickupType.DELIVERY).shippingAddress("台北市信義區")
                .subtotalAmount(new BigDecimal("760")).shippingFee(new BigDecimal("100"))
                .totalAmount(new BigDecimal("860.00"))
                .notes("[前台訂單] 付款方式：線上付款（綠界）")
                .items(List.of(OrderItemDTO.builder().productName("日式茶杯").productSpec("藍色")
                        .quantity(2).subtotalAmount(new BigDecimal("760")).build()))
                .build();
    }

    @Test
    void sendsConfirmationWithItemsTotalsAndLookupLink() {
        when(orderNotificationService.isEmailEnabled()).thenReturn(true);
        when(orderService.getOrder(7L)).thenReturn(order);
        ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);

        listener.onOrderEmail(new OrderEmailEvent(7L, OrderEmailEvent.Type.CREATED));

        verify(orderNotificationService).sendNotification(eq(7L), eq(NotificationType.EMAIL), eq("buyer@example.com"),
                subject.capture(), body.capture());
        assertThat(subject.getValue()).contains("訂單成立", "ORD2026092400000001");
        assertThat(body.getValue())
                .contains("日式茶杯（藍色） × 2", "訂單總額：NT$ 860", "前往付款",
                        "https://shop.example.com/shop/order/lookup?orderNumber=ORD2026092400000001&email=buyer%40example.com");
    }

    @Test
    void skipsWhenMailNotConfigured() {
        when(orderNotificationService.isEmailEnabled()).thenReturn(false);

        listener.onOrderEmail(new OrderEmailEvent(7L, OrderEmailEvent.Type.PAID));

        verifyNoInteractions(orderService);
        verify(orderNotificationService, never()).sendNotification(any(), any(), any(), any(), any());
    }

    @Test
    void cancelledEmailWording() {
        assertThat(listener.subject(OrderEmailEvent.Type.CANCELLED, order)).contains("訂單取消");
        assertThat(listener.body(OrderEmailEvent.Type.CANCELLED, order)).contains("您的訂單已取消").doesNotContain("前往付款");
    }
}
