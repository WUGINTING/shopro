package com.info.ecommerce.modules.order.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import com.info.ecommerce.modules.order.entity.OrderNotification;
import com.info.ecommerce.modules.order.enums.NotificationType;
import com.info.ecommerce.modules.order.repository.OrderNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderNotificationService
 */
@ExtendWith(MockitoExtension.class)
class OrderNotificationServiceTest {

    @Mock
    private OrderNotificationRepository orderNotificationRepository;

    @Mock
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private OrderNotificationService orderNotificationService;

    private OrderNotification emailNotification;
    private OrderNotification smsNotification;

    @BeforeEach
    void setUp() {
        // 預設已設定郵件伺服器
        lenient().when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        lenient().when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        emailNotification = OrderNotification.builder()
                .id(1L)
                .orderId(1L)
                .notificationType(NotificationType.EMAIL)
                .recipient("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .isSent(true)
                .build();

        smsNotification = OrderNotification.builder()
                .id(2L)
                .orderId(1L)
                .notificationType(NotificationType.SMS)
                .recipient("0912345678")
                .subject(null)
                .content("Test SMS Content")
                .isSent(true)
                .build();
    }

    @Test
    void should_SendEmailNotification_When_NotificationTypeIsEmail() {
        // given
        ArgumentCaptor<OrderNotification> notificationCaptor = ArgumentCaptor.forClass(OrderNotification.class);
        when(orderNotificationRepository.save(any(OrderNotification.class))).thenReturn(emailNotification);

        // when
        orderNotificationService.sendNotification(
                1L,
                NotificationType.EMAIL,
                "test@example.com",
                "Test Subject",
                "Test Content"
        );

        // then
        verify(orderNotificationRepository, times(1)).save(notificationCaptor.capture());
        OrderNotification saved = notificationCaptor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(1L);
        assertThat(saved.getNotificationType()).isEqualTo(NotificationType.EMAIL);
        assertThat(saved.getRecipient()).isEqualTo("test@example.com");
        assertThat(saved.getSubject()).isEqualTo("Test Subject");
        assertThat(saved.getContent()).isEqualTo("Test Content");
        assertThat(saved.getIsSent()).isTrue();
        assertThat(saved.getSentAt()).isNotNull();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void should_RecordUnsent_When_MailServerNotConfigured() {
        when(mailSenderProvider.getIfAvailable()).thenReturn(null);
        ArgumentCaptor<OrderNotification> captor = ArgumentCaptor.forClass(OrderNotification.class);

        orderNotificationService.sendNotification(1L, NotificationType.EMAIL, "test@example.com", "S", "C");

        verify(orderNotificationRepository).save(captor.capture());
        assertThat(captor.getValue().getIsSent()).isFalse();
        assertThat(captor.getValue().getErrorMessage()).contains("郵件伺服器");
        assertThat(orderNotificationService.isEmailEnabled()).isFalse();
    }

    @Test
    void should_SendSmsNotification_When_NotificationTypeIsSms() {
        // given
        ArgumentCaptor<OrderNotification> notificationCaptor = ArgumentCaptor.forClass(OrderNotification.class);
        when(orderNotificationRepository.save(any(OrderNotification.class))).thenReturn(smsNotification);

        // when
        orderNotificationService.sendNotification(
                1L,
                NotificationType.SMS,
                "0912345678",
                null,
                "Test SMS Content"
        );

        // then
        verify(orderNotificationRepository, times(1)).save(notificationCaptor.capture());
        OrderNotification saved = notificationCaptor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(1L);
        assertThat(saved.getNotificationType()).isEqualTo(NotificationType.SMS);
        assertThat(saved.getRecipient()).isEqualTo("0912345678");
        assertThat(saved.getContent()).isEqualTo("Test SMS Content");
        // 尚未串接簡訊服務：如實記錄為未寄出
        assertThat(saved.getIsSent()).isFalse();
        assertThat(saved.getErrorMessage()).contains("簡訊");
    }

    @Test
    void should_HandleException_When_NotificationSendingFails() {
        // given
        doThrow(new MailSendException("SMTP down")).when(mailSender).send(any(MimeMessage.class));
        ArgumentCaptor<OrderNotification> notificationCaptor = ArgumentCaptor.forClass(OrderNotification.class);
        when(orderNotificationRepository.save(notificationCaptor.capture())).thenReturn(emailNotification);

        // when
        orderNotificationService.sendNotification(
                1L,
                NotificationType.EMAIL,
                "test@example.com",
                "Test Subject",
                "Test Content"
        );

        // then
        OrderNotification saved = notificationCaptor.getValue();
        assertThat(saved.getIsSent()).isFalse();
        assertThat(saved.getErrorMessage()).contains("SMTP down");
        verify(orderNotificationRepository, times(1)).save(any(OrderNotification.class));
    }

    @Test
    void should_SendOrderStatusChangeNotification_When_Called() {
        // given
        when(orderNotificationRepository.save(any(OrderNotification.class))).thenReturn(emailNotification);

        // when
        orderNotificationService.sendOrderStatusChangeNotification(
                1L,
                "test@example.com",
                "ORD001",
                "COMPLETED"
        );

        // then
        verify(orderNotificationRepository, times(1)).save(any(OrderNotification.class));
    }

    @Test
    void should_SendOrderCreatedNotification_When_Called() {
        // given
        when(orderNotificationRepository.save(any(OrderNotification.class))).thenReturn(emailNotification);

        // when
        orderNotificationService.sendOrderCreatedNotification(
                1L,
                "test@example.com",
                "ORD001"
        );

        // then
        verify(orderNotificationRepository, times(1)).save(any(OrderNotification.class));
    }

    @Test
    void should_SendStorePickupNotification_When_Called() {
        // given
        when(orderNotificationRepository.save(any(OrderNotification.class))).thenReturn(smsNotification);

        // when
        orderNotificationService.sendStorePickupNotification(
                1L,
                "0912345678",
                "ORD001",
                "Store A"
        );

        // then
        verify(orderNotificationRepository, times(1)).save(any(OrderNotification.class));
    }

    @Test
    void should_RetryFailedNotifications_When_Called() {
        // given
        OrderNotification failedNotification1 = OrderNotification.builder()
                .id(3L)
                .orderId(2L)
                .notificationType(NotificationType.EMAIL)
                .recipient("failed@example.com")
                .subject("Failed Subject")
                .content("Failed Content")
                .isSent(false)
                .errorMessage("Previous error")
                .build();

        OrderNotification failedNotification2 = OrderNotification.builder()
                .id(4L)
                .orderId(3L)
                .notificationType(NotificationType.SMS)
                .recipient("0987654321")
                .content("Failed SMS")
                .isSent(false)
                .build();

        List<OrderNotification> failedNotifications = List.of(failedNotification1, failedNotification2);
        when(orderNotificationRepository.findByIsSent(false)).thenReturn(failedNotifications);
        when(orderNotificationRepository.save(any(OrderNotification.class)))
                .thenReturn(failedNotification1, failedNotification2);

        // when
        orderNotificationService.retryFailedNotifications();

        // then：只重送 Email，並更新原記錄（不新增）
        verify(orderNotificationRepository, times(1)).findByIsSent(false);
        verify(orderNotificationRepository, times(1)).save(failedNotification1);
        verify(orderNotificationRepository, never()).save(failedNotification2);
        assertThat(failedNotification1.getIsSent()).isTrue();
    }

    @Test
    void should_RetryFailedNotifications_When_NoFailedNotifications() {
        // given
        when(orderNotificationRepository.findByIsSent(false)).thenReturn(new ArrayList<>());

        // when
        orderNotificationService.retryFailedNotifications();

        // then
        verify(orderNotificationRepository, times(1)).findByIsSent(false);
        verify(orderNotificationRepository, never()).save(any(OrderNotification.class));
    }

    @Test
    void should_GetNotificationsByOrderId_When_Called() {
        // given
        List<OrderNotification> notifications = List.of(emailNotification, smsNotification);
        when(orderNotificationRepository.findByOrderId(1L)).thenReturn(notifications);

        // when
        List<OrderNotification> result = orderNotificationService.getNotificationsByOrderId(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(emailNotification, smsNotification);
        verify(orderNotificationRepository, times(1)).findByOrderId(1L);
    }

    @Test
    void should_GetEmptyList_When_GetNotificationsByOrderIdWithNoNotifications() {
        // given
        when(orderNotificationRepository.findByOrderId(1L)).thenReturn(new ArrayList<>());

        // when
        List<OrderNotification> result = orderNotificationService.getNotificationsByOrderId(1L);

        // then
        assertThat(result).isEmpty();
        verify(orderNotificationRepository, times(1)).findByOrderId(1L);
    }
}
