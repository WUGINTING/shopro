package com.info.ecommerce.modules.product.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.product.entity.InventoryAlert;
import com.info.ecommerce.modules.product.entity.InventoryMovementLog;
import com.info.ecommerce.modules.product.entity.ProductInventory;
import com.info.ecommerce.modules.product.entity.StockNotification;
import com.info.ecommerce.modules.product.enums.AlertLevel;
import com.info.ecommerce.modules.product.repository.InventoryAlertRepository;
import com.info.ecommerce.modules.product.repository.InventoryMovementLogRepository;
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
    private final InventoryMovementLogRepository movementLogRepository;
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
     * 取得庫存異動紀錄（最近 100 筆）
     */
    public List<InventoryMovementLog> getInventoryMovementLogs() {
        return movementLogRepository.findTop100ByOrderByCreatedAtDesc();
    }

    /**
     * 取得單一商品庫存異動紀錄（最近 100 筆）
     */
    public List<InventoryMovementLog> getProductInventoryMovementLogs(Long productId) {
        return movementLogRepository.findTop100ByProductIdOrderByCreatedAtDesc(productId);
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
     * 更新庫存（如果不存在則創建新記錄）
     */
    @Transactional
    public void updateInventory(Long productId, Long specificationId,
                                Long warehouseId, Integer quantity) {
        adjustInventory(productId, specificationId, warehouseId, quantity,
                "INVENTORY_API", "後台人工庫存調整");
    }

    /**
     * 根據來源與備註調整庫存並回傳最新庫存資料
     */
    @Transactional
    public ProductInventory adjustInventory(Long productId, Long specificationId,
                                            Long warehouseId, Integer quantity,
                                            String source, String remark) {
        return changeInventory(productId, specificationId, warehouseId, quantity, source, remark);
    }

    private ProductInventory changeInventory(Long productId, Long specificationId,
                                             Long warehouseId, Integer quantity,
                                             String source, String remark) {
        if (productId == null || quantity == null) {
            throw new BusinessException("商品編號或數量不能為空");
        }

        ProductInventory inventory = inventoryRepository
                .findByProductIdAndSpecificationId(productId, specificationId)
                .orElse(null);
        int beforeStock = inventory != null && inventory.getAvailableStock() != null
                ? inventory.getAvailableStock() : 0;
        Long resolvedWarehouseId = warehouseId != null ? warehouseId : 1L;

        if (inventory == null) {
            inventory = ProductInventory.builder()
                    .productId(productId)
                    .specificationId(specificationId)
                    .warehouseId(resolvedWarehouseId)
                    .availableStock(quantity)
                    .lockedStock(0)
                    .safetyStock(10)
                    .build();
        } else {
            int currentStock = inventory.getAvailableStock() != null ? inventory.getAvailableStock() : 0;
            inventory.setAvailableStock(currentStock + quantity);
            if (inventory.getWarehouseId() == null) {
                inventory.setWarehouseId(resolvedWarehouseId);
            }
        }

        inventoryRepository.save(inventory);
        int afterStock = inventory.getAvailableStock() != null ? inventory.getAvailableStock() : 0;
        saveInventoryMovementLog(productId, specificationId, inventory.getWarehouseId(),
                quantity, beforeStock, afterStock, source, remark);

        if (inventory.getAvailableStock() > 0) {
            List<InventoryAlert> alerts = alertRepository.findByProductIdAndResolvedFalse(productId);
            for (InventoryAlert alert : alerts) {
                alert.setResolved(true);
                alert.setResolvedAt(LocalDateTime.now());
            }
            alertRepository.saveAll(alerts);
        }

        if (quantity > 0) {
            processStockNotifications(productId);
        }

        return inventory;
    }

    private void saveInventoryMovementLog(Long productId, Long specificationId, Long warehouseId,
                                          Integer quantity, int beforeStock, int afterStock,
                                          String source, String remark) {
        int changeQty = quantity != null ? quantity : (afterStock - beforeStock);
        String changeType = changeQty > 0 ? "INCREASE" : (changeQty < 0 ? "DECREASE" : "SET");

        InventoryMovementLog log = InventoryMovementLog.builder()
                .productId(productId)
                .specificationId(specificationId)
                .warehouseId(warehouseId)
                .changeType(changeType)
                .source(source)
                .changeQuantity(changeQty)
                .beforeStock(beforeStock)
                .afterStock(afterStock)
                .remark(remark)
                .build();

        movementLogRepository.save(log);
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
