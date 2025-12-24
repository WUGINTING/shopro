package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.entity.InventoryAlert;
import com.info.ecommerce.modules.product.entity.ProductInventory;
import com.info.ecommerce.modules.product.entity.StockNotification;
import com.info.ecommerce.modules.product.enums.AlertLevel;
import com.info.ecommerce.modules.product.repository.InventoryAlertRepository;
import com.info.ecommerce.modules.product.repository.ProductInventoryRepository;
import com.info.ecommerce.modules.product.repository.StockNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryManagementService
 */
@ExtendWith(MockitoExtension.class)
class InventoryManagementServiceTest {

    @Mock
    private ProductInventoryRepository inventoryRepository;

    @Mock
    private InventoryAlertRepository alertRepository;

    @Mock
    private StockNotificationRepository notificationRepository;

    @InjectMocks
    private InventoryManagementService inventoryManagementService;

    private ProductInventory outOfStockInventory;
    private ProductInventory criticalInventory;
    private ProductInventory lowInventory;
    private ProductInventory normalInventory;
    private InventoryAlert alert;
    private StockNotification stockNotification;

    @BeforeEach
    void setUp() {
        outOfStockInventory = ProductInventory.builder()
                .id(1L)
                .productId(1L)
                .specificationId(1L)
                .warehouseId(1L)
                .availableStock(0)
                .lockedStock(0)
                .safetyStock(10)
                .build();

        criticalInventory = ProductInventory.builder()
                .id(2L)
                .productId(2L)
                .specificationId(2L)
                .warehouseId(1L)
                .availableStock(4)
                .lockedStock(0)
                .safetyStock(10)
                .build();

        lowInventory = ProductInventory.builder()
                .id(3L)
                .productId(3L)
                .specificationId(3L)
                .warehouseId(1L)
                .availableStock(8)
                .lockedStock(0)
                .safetyStock(10)
                .build();

        normalInventory = ProductInventory.builder()
                .id(4L)
                .productId(4L)
                .specificationId(4L)
                .warehouseId(1L)
                .availableStock(20)
                .lockedStock(0)
                .safetyStock(10)
                .build();

        alert = InventoryAlert.builder()
                .id(1L)
                .productId(1L)
                .specificationId(1L)
                .alertLevel(AlertLevel.OUT_OF_STOCK)
                .currentStock(0)
                .safetyStock(10)
                .message("商品 ID 1 已無庫存")
                .resolved(false)
                .build();

        stockNotification = StockNotification.builder()
                .id(1L)
                .productId(1L)
                .specificationId(1L)
                .userEmail("test@example.com")
                .userPhone("0912345678")
                .notified(false)
                .build();
    }

    @Test
    void should_CreateAlerts_When_CheckInventoryWithLowStock() {
        // given
        List<ProductInventory> inventories = List.of(
                outOfStockInventory,
                criticalInventory,
                lowInventory,
                normalInventory
        );
        when(inventoryRepository.findAll()).thenReturn(inventories);
        when(alertRepository.findByProductIdAndResolvedFalse(anyLong())).thenReturn(new ArrayList<>());
        when(alertRepository.save(any(InventoryAlert.class))).thenReturn(alert);

        // when
        inventoryManagementService.checkInventoryAndCreateAlerts();

        // then
        verify(inventoryRepository, times(1)).findAll();
        verify(alertRepository, times(3)).save(any(InventoryAlert.class));
    }

    @Test
    void should_NotCreateDuplicateAlert_When_ExistingUnresolvedAlertExists() {
        // given
        List<ProductInventory> inventories = List.of(outOfStockInventory);
        List<InventoryAlert> existingAlerts = List.of(alert);
        when(inventoryRepository.findAll()).thenReturn(inventories);
        when(alertRepository.findByProductIdAndResolvedFalse(1L)).thenReturn(existingAlerts);

        // when
        inventoryManagementService.checkInventoryAndCreateAlerts();

        // then
        verify(inventoryRepository, times(1)).findAll();
        verify(alertRepository, times(1)).findByProductIdAndResolvedFalse(1L);
        verify(alertRepository, never()).save(any(InventoryAlert.class));
    }

    @Test
    void should_CreateOutOfStockAlert_When_AvailableStockIsZero() {
        // given
        List<ProductInventory> inventories = List.of(outOfStockInventory);
        when(inventoryRepository.findAll()).thenReturn(inventories);
        when(alertRepository.findByProductIdAndResolvedFalse(1L)).thenReturn(new ArrayList<>());
        
        ArgumentCaptor<InventoryAlert> alertCaptor = ArgumentCaptor.forClass(InventoryAlert.class);
        when(alertRepository.save(alertCaptor.capture())).thenReturn(alert);

        // when
        inventoryManagementService.checkInventoryAndCreateAlerts();

        // then
        InventoryAlert savedAlert = alertCaptor.getValue();
        assertThat(savedAlert.getAlertLevel()).isEqualTo(AlertLevel.OUT_OF_STOCK);
        assertThat(savedAlert.getCurrentStock()).isEqualTo(0);
        verify(alertRepository, times(1)).save(any(InventoryAlert.class));
    }

    @Test
    void should_CreateCriticalAlert_When_StockBelowHalfSafetyStock() {
        // given
        List<ProductInventory> inventories = List.of(criticalInventory);
        when(inventoryRepository.findAll()).thenReturn(inventories);
        when(alertRepository.findByProductIdAndResolvedFalse(2L)).thenReturn(new ArrayList<>());
        
        ArgumentCaptor<InventoryAlert> alertCaptor = ArgumentCaptor.forClass(InventoryAlert.class);
        when(alertRepository.save(alertCaptor.capture())).thenReturn(alert);

        // when
        inventoryManagementService.checkInventoryAndCreateAlerts();

        // then
        InventoryAlert savedAlert = alertCaptor.getValue();
        assertThat(savedAlert.getAlertLevel()).isEqualTo(AlertLevel.CRITICAL);
        verify(alertRepository, times(1)).save(any(InventoryAlert.class));
    }

    @Test
    void should_CreateLowAlert_When_StockBelowSafetyStock() {
        // given
        List<ProductInventory> inventories = List.of(lowInventory);
        when(inventoryRepository.findAll()).thenReturn(inventories);
        when(alertRepository.findByProductIdAndResolvedFalse(3L)).thenReturn(new ArrayList<>());
        
        ArgumentCaptor<InventoryAlert> alertCaptor = ArgumentCaptor.forClass(InventoryAlert.class);
        when(alertRepository.save(alertCaptor.capture())).thenReturn(alert);

        // when
        inventoryManagementService.checkInventoryAndCreateAlerts();

        // then
        InventoryAlert savedAlert = alertCaptor.getValue();
        assertThat(savedAlert.getAlertLevel()).isEqualTo(AlertLevel.LOW);
        verify(alertRepository, times(1)).save(any(InventoryAlert.class));
    }

    @Test
    void should_ResolveAlert_When_AlertExists() {
        // given
        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(InventoryAlert.class))).thenReturn(alert);

        // when
        inventoryManagementService.resolveAlert(1L);

        // then
        verify(alertRepository, times(1)).findById(1L);
        verify(alertRepository, times(1)).save(any(InventoryAlert.class));
    }

    @Test
    void should_ThrowBusinessException_When_ResolveNonExistentAlert() {
        // given
        when(alertRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inventoryManagementService.resolveAlert(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("警示不存在");
        verify(alertRepository, times(1)).findById(1L);
        verify(alertRepository, never()).save(any(InventoryAlert.class));
    }

    @Test
    void should_GetUnresolvedAlerts_When_Called() {
        // given
        List<InventoryAlert> alerts = List.of(alert);
        when(alertRepository.findByResolvedFalse()).thenReturn(alerts);

        // when
        List<InventoryAlert> result = inventoryManagementService.getUnresolvedAlerts();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getResolved()).isFalse();
        verify(alertRepository, times(1)).findByResolvedFalse();
    }

    @Test
    void should_GetProductUnresolvedAlerts_When_Called() {
        // given
        List<InventoryAlert> alerts = List.of(alert);
        when(alertRepository.findByProductIdAndResolvedFalse(1L)).thenReturn(alerts);

        // when
        List<InventoryAlert> result = inventoryManagementService.getProductUnresolvedAlerts(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        verify(alertRepository, times(1)).findByProductIdAndResolvedFalse(1L);
    }

    @Test
    void should_SubscribeStockNotification_When_Called() {
        // given
        when(notificationRepository.save(any(StockNotification.class))).thenReturn(stockNotification);

        // when
        inventoryManagementService.subscribeStockNotification(
                1L,
                1L,
                "test@example.com",
                "0912345678"
        );

        // then
        verify(notificationRepository, times(1)).save(any(StockNotification.class));
    }

    @Test
    void should_ProcessStockNotifications_When_NotificationsExist() {
        // given
        List<StockNotification> notifications = List.of(stockNotification);
        when(notificationRepository.findByProductIdAndNotifiedFalse(1L)).thenReturn(notifications);
        when(notificationRepository.saveAll(anyList())).thenReturn(notifications);

        // when
        inventoryManagementService.processStockNotifications(1L);

        // then
        verify(notificationRepository, times(1)).findByProductIdAndNotifiedFalse(1L);
        verify(notificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void should_NotProcessNotifications_When_NoNotificationsExist() {
        // given
        when(notificationRepository.findByProductIdAndNotifiedFalse(1L)).thenReturn(new ArrayList<>());

        // when
        inventoryManagementService.processStockNotifications(1L);

        // then
        verify(notificationRepository, times(1)).findByProductIdAndNotifiedFalse(1L);
        verify(notificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void should_UpdateInventory_When_ValidInventoryExists() {
        // given
        when(inventoryRepository.findByProductIdAndSpecificationId(1L, 1L))
                .thenReturn(Optional.of(outOfStockInventory));
        when(inventoryRepository.save(any(ProductInventory.class))).thenReturn(outOfStockInventory);
        when(notificationRepository.findByProductIdAndNotifiedFalse(1L)).thenReturn(new ArrayList<>());
        when(notificationRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // when
        inventoryManagementService.updateInventory(1L, 1L, 1L, 50);

        // then
        verify(inventoryRepository, times(1)).findByProductIdAndSpecificationId(1L, 1L);
        verify(inventoryRepository, times(1)).save(any(ProductInventory.class));
        verify(notificationRepository, times(1)).findByProductIdAndNotifiedFalse(1L);
    }

    @Test
    void should_ThrowBusinessException_When_UpdateNonExistentInventory() {
        // given
        when(inventoryRepository.findByProductIdAndSpecificationId(1L, 1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inventoryManagementService.updateInventory(1L, 1L, 1L, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("庫存記錄不存在");
        verify(inventoryRepository, times(1)).findByProductIdAndSpecificationId(1L, 1L);
        verify(inventoryRepository, never()).save(any(ProductInventory.class));
    }

    @Test
    void should_ProcessNotifications_When_UpdateInventoryToPositiveQuantity() {
        // given
        when(inventoryRepository.findByProductIdAndSpecificationId(1L, 1L))
                .thenReturn(Optional.of(outOfStockInventory));
        when(inventoryRepository.save(any(ProductInventory.class))).thenReturn(outOfStockInventory);
        
        List<StockNotification> notifications = List.of(stockNotification);
        when(notificationRepository.findByProductIdAndNotifiedFalse(1L)).thenReturn(notifications);
        when(notificationRepository.saveAll(anyList())).thenReturn(notifications);

        // when
        inventoryManagementService.updateInventory(1L, 1L, 1L, 10);

        // then
        verify(notificationRepository, times(1)).findByProductIdAndNotifiedFalse(1L);
        verify(notificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void should_NotProcessNotifications_When_UpdateInventoryToZeroQuantity() {
        // given
        when(inventoryRepository.findByProductIdAndSpecificationId(1L, 1L))
                .thenReturn(Optional.of(normalInventory));
        when(inventoryRepository.save(any(ProductInventory.class))).thenReturn(normalInventory);

        // when
        inventoryManagementService.updateInventory(1L, 1L, 1L, 0);

        // then
        verify(inventoryRepository, times(1)).save(any(ProductInventory.class));
        verify(notificationRepository, never()).findByProductIdAndNotifiedFalse(anyLong());
    }
}
