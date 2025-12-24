package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.entity.InventoryAlert;
import com.info.ecommerce.modules.product.entity.ProductInventory;
import com.info.ecommerce.modules.product.entity.StockNotification;
import com.info.ecommerce.modules.product.enums.AlertLevel;
import com.info.ecommerce.modules.product.repository.InventoryAlertRepository;
import com.info.ecommerce.modules.product.repository.ProductInventoryRepository;
import com.info.ecommerce.modules.product.repository.StockNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 庫存管理服務
 * 支持庫存警示、貨到通知等功能
 */
@Service
@RequiredArgsConstructor
public class InventoryManagementService {

    private final ProductInventoryRepository inventoryRepository;
    private final InventoryAlertRepository alertRepository;
    private final StockNotificationRepository notificationRepository;

    /**
     * 檢查庫存並創建警示
     */
    @Transactional
    public void checkInventoryAndCreateAlerts() {
        List<ProductInventory> inventories = inventoryRepository.findAll();
        
        for (ProductInventory inventory : inventories) {
            AlertLevel alertLevel = inventory.checkAlertLevel();
            
            if (alertLevel != null) {
                // 檢查是否已有未解決的警示
                List<InventoryAlert> existingAlerts = alertRepository
                        .findByProductIdAndResolvedFalse(inventory.getProductId());
                
                if (existingAlerts.isEmpty()) {
                    // 創建新警示
                    InventoryAlert alert = InventoryAlert.builder()
                            .productId(inventory.getProductId())
                            .specificationId(inventory.getSpecificationId())
                            .alertLevel(alertLevel)
                            .currentStock(inventory.getAvailableStock())
                            .safetyStock(inventory.getSafetyStock())
                            .message(generateAlertMessage(alertLevel, inventory))
                            .resolved(false)
                            .build();
                    
                    alertRepository.save(alert);
                }
            }
        }
    }

    /**
     * 解決庫存警示
     */
    @Transactional
    public void resolveAlert(Long alertId) {
        InventoryAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new BusinessException("警示不存在"));
        
        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alertRepository.save(alert);
    }

    /**
     * 取得未解決的警示
     */
    public List<InventoryAlert> getUnresolvedAlerts() {
        return alertRepository.findByResolvedFalse();
    }

    /**
     * 取得指定商品的未解決警示
     */
    public List<InventoryAlert> getProductUnresolvedAlerts(Long productId) {
        return alertRepository.findByProductIdAndResolvedFalse(productId);
    }

    /**
     * 訂閱貨到通知
     */
    @Transactional
    public void subscribeStockNotification(Long productId, Long specificationId, 
                                          String email, String phone) {
        StockNotification notification = StockNotification.builder()
                .productId(productId)
                .specificationId(specificationId)
                .userEmail(email)
                .userPhone(phone)
                .notified(false)
                .build();
        
        notificationRepository.save(notification);
    }

    /**
     * 處理貨到通知
     */
    @Transactional
    public void processStockNotifications(Long productId) {
        List<StockNotification> notifications = 
                notificationRepository.findByProductIdAndNotifiedFalse(productId);
        
        for (StockNotification notification : notifications) {
            // TODO: 整合郵件或簡訊服務來發送實際通知
            // 暫時只標記為已通知
            notification.setNotified(true);
            notification.setNotifiedAt(LocalDateTime.now());
        }
        
        notificationRepository.saveAll(notifications);
    }

    /**
     * 更新庫存
     */
    @Transactional
    public void updateInventory(Long productId, Long specificationId, 
                               Long warehouseId, Integer quantity) {
        ProductInventory inventory = inventoryRepository
                .findByProductIdAndSpecificationId(productId, specificationId)
                .orElseThrow(() -> new BusinessException("庫存記錄不存在"));
        
        inventory.setAvailableStock(quantity);
        inventoryRepository.save(inventory);
        
        // 檢查是否需要發送貨到通知
        if (quantity > 0) {
            processStockNotifications(productId);
        }
    }

    private String generateAlertMessage(AlertLevel level, ProductInventory inventory) {
        return switch (level) {
            case OUT_OF_STOCK -> String.format("商品 ID %d 已無庫存", inventory.getProductId());
            case CRITICAL -> String.format("商品 ID %d 庫存嚴重不足，當前庫存: %d", 
                    inventory.getProductId(), inventory.getAvailableStock());
            case LOW -> String.format("商品 ID %d 庫存偏低，當前庫存: %d，安全庫存: %d", 
                    inventory.getProductId(), inventory.getAvailableStock(), inventory.getSafetyStock());
        };
    }
}
