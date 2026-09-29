package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.entity.InventoryAlert;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductInventory;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.entity.StockNotification;
import com.info.ecommerce.modules.product.enums.AlertLevel;
import com.info.ecommerce.modules.product.event.StockChangedEvent;
import com.info.ecommerce.modules.product.repository.*;
import com.info.ecommerce.modules.system.enums.AdminNotificationType;
import com.info.ecommerce.modules.system.service.AdminNotificationService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InventoryManagementServiceTest {

    @Mock private ProductInventoryRepository inventoryRepository;
    @Mock private InventoryAlertRepository alertRepository;
    @Mock private InventoryMovementLogRepository movementLogRepository;
    @Mock private StockNotificationRepository notificationRepository;
    @Mock private AdminNotificationService adminNotificationService;
    @Mock private ProductSpecificationRepository specificationRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private ObjectProvider<JavaMailSender> mailSenderProvider;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private InventoryManagementService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "lowStockThreshold", 5);
        ReflectionTestUtils.setField(service, "storeName", "測試商店");
        ReflectionTestUtils.setField(service, "mailFrom", "");
        ReflectionTestUtils.setField(service, "storefrontUrl", "https://shop.example.com");
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(Product.builder().id(1L).name("茶杯").build()));
        when(specificationRepository.findByProductId(anyLong())).thenReturn(List.of());
        when(alertRepository.findByProductIdAndResolvedFalse(anyLong())).thenReturn(List.of());
    }

    private void productStock(Integer stock, Integer safety) {
        when(inventoryRepository.findByProductIdAndSpecificationId(1L, null)).thenReturn(Optional.of(
                ProductInventory.builder().productId(1L).availableStock(stock).safetyStock(safety).build()));
    }

    @Test
    void outOfStockProduct_createsAlertAndNotifiesStaff() {
        productStock(0, 10);

        service.checkProductAlerts(List.of(1L));

        ArgumentCaptor<InventoryAlert> alert = ArgumentCaptor.forClass(InventoryAlert.class);
        verify(alertRepository).save(alert.capture());
        assertThat(alert.getValue().getAlertLevel()).isEqualTo(AlertLevel.OUT_OF_STOCK);
        assertThat(alert.getValue().getMessage()).contains("茶杯");
        verify(adminNotificationService).createNotification(eq(AdminNotificationType.STOCK_LOW), isNull(), eq(1L), eq("商品已售完"), anyString());
    }

    @Test
    void stockLevels_followSafetyStock() {
        productStock(4, 10);
        service.checkProductAlerts(List.of(1L));
        ArgumentCaptor<InventoryAlert> alert = ArgumentCaptor.forClass(InventoryAlert.class);
        verify(alertRepository).save(alert.capture());
        assertThat(alert.getValue().getAlertLevel()).isEqualTo(AlertLevel.CRITICAL);

        reset(alertRepository);
        when(alertRepository.findByProductIdAndResolvedFalse(anyLong())).thenReturn(List.of());
        productStock(8, 10);
        service.checkProductAlerts(List.of(1L));
        verify(alertRepository).save(alert.capture());
        assertThat(alert.getValue().getAlertLevel()).isEqualTo(AlertLevel.LOW);
    }

    @Test
    void untrackedOrHealthyStock_resolvesExistingAlerts_andCreatesNone() {
        InventoryAlert open = InventoryAlert.builder().productId(1L).alertLevel(AlertLevel.LOW).resolved(false).build();
        when(alertRepository.findByProductIdAndResolvedFalse(1L)).thenReturn(List.of(open));
        productStock(50, 10);

        service.checkProductAlerts(List.of(1L));

        assertThat(open.getResolved()).isTrue();
        verify(alertRepository, never()).save(any());
        verify(adminNotificationService, never()).createNotification(any(), any(), any(), any(), any());
    }

    @Test
    void existingAlert_isUpdatedNotDuplicated() {
        InventoryAlert open = InventoryAlert.builder().productId(1L).alertLevel(AlertLevel.LOW).resolved(false).build();
        when(alertRepository.findByProductIdAndResolvedFalse(1L)).thenReturn(List.of(open));
        productStock(3, 10);

        service.checkProductAlerts(List.of(1L));

        assertThat(open.getAlertLevel()).isEqualTo(AlertLevel.CRITICAL);
        verify(alertRepository).save(open);
        verify(adminNotificationService, never()).createNotification(any(), any(), any(), any(), any());
    }

    @Test
    void specProducts_useSpecStockAndThreshold() {
        when(specificationRepository.findByProductId(1L)).thenReturn(List.of(
                ProductSpecification.builder().id(10L).productId(1L).specName("藍色").stock(2).enabled(true).build(),
                ProductSpecification.builder().id(11L).productId(1L).specName("紅色").stock(null).enabled(true).build()));

        service.checkProductAlerts(List.of(1L));

        ArgumentCaptor<InventoryAlert> alert = ArgumentCaptor.forClass(InventoryAlert.class);
        verify(alertRepository, times(1)).save(alert.capture());
        assertThat(alert.getValue().getSpecificationId()).isEqualTo(10L);
        assertThat(alert.getValue().getMessage()).contains("藍色");
    }

    @Test
    void restockingASpec_updatesSpecStock_logsAndPublishesEvent() {
        ProductSpecification spec = ProductSpecification.builder().id(10L).productId(1L).stock(1).build();
        when(specificationRepository.findById(10L)).thenReturn(Optional.of(spec));
        when(inventoryRepository.findByProductIdAndSpecificationId(1L, 10L)).thenReturn(Optional.empty());

        service.updateInventory(1L, 10L, 1L, 20);

        assertThat(spec.getStock()).isEqualTo(21);
        verify(movementLogRepository).save(argThat(log -> log.getBeforeStock() == 1 && log.getAfterStock() == 21));
        verify(eventPublisher).publishEvent(new StockChangedEvent(List.of(1L), true));
    }

    @Test
    void stockNotifications_areKeptWhenMailIsNotConfigured() {
        StockNotification subscription = StockNotification.builder().id(5L).productId(1L).userEmail("a@example.com").notified(false).build();
        when(notificationRepository.findByProductIdAndNotifiedFalse(1L)).thenReturn(List.of(subscription));
        when(mailSenderProvider.getIfAvailable()).thenReturn(null);

        service.processStockNotifications(1L);

        assertThat(subscription.getNotified()).isFalse();
        verify(notificationRepository, never()).saveAll(any());
    }

    @Test
    void stockNotifications_areEmailedOnlyWhenInStock() {
        StockNotification subscription = StockNotification.builder().id(5L).productId(1L).userEmail("a@example.com").notified(false).build();
        when(notificationRepository.findByProductIdAndNotifiedFalse(1L)).thenReturn(List.of(subscription));
        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        when(mailSender.createMimeMessage()).thenAnswer(invocation -> new MimeMessage((Session) null));

        productStock(0, 10);
        service.processStockNotifications(1L);
        verify(notificationRepository, never()).claim(anyLong(), any());
        verify(mailSender, never()).send(any(MimeMessage.class));

        productStock(3, 10);
        when(notificationRepository.claim(eq(5L), any())).thenReturn(1);
        service.processStockNotifications(1L);
        verify(mailSender).send(any(MimeMessage.class));

        // 另一個執行緒已經寄出（搶不到）時不再寄信
        when(notificationRepository.claim(eq(5L), any())).thenReturn(0);
        service.processStockNotifications(1L);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void subscribe_validatesEmail_andIgnoresDuplicates() {
        assertThatThrownBy(() -> service.subscribeStockNotification(1L, null, "not-an-email", null))
                .isInstanceOf(BusinessException.class);

        when(notificationRepository.findByProductIdAndNotifiedFalse(1L)).thenReturn(List.of(
                StockNotification.builder().productId(1L).userEmail("A@example.com").notified(false).build()));
        service.subscribeStockNotification(1L, null, "a@example.com", null);
        verify(notificationRepository, never()).save(any());

        when(specificationRepository.findById(10L)).thenReturn(java.util.Optional.of(
                ProductSpecification.builder().id(10L).productId(1L).build()));
        service.subscribeStockNotification(1L, 10L, "a@example.com", null);
        verify(notificationRepository).save(any());

        // 規格不屬於此商品
        when(specificationRepository.findById(11L)).thenReturn(java.util.Optional.of(
                ProductSpecification.builder().id(11L).productId(2L).build()));
        assertThatThrownBy(() -> service.subscribeStockNotification(1L, 11L, "b@example.com", null))
                .isInstanceOf(BusinessException.class);
    }
}
