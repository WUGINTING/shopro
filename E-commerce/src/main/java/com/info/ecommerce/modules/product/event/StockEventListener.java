package com.info.ecommerce.modules.product.event;

import com.info.ecommerce.modules.product.service.InventoryManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 庫存變動後（交易提交後、非同步）更新低庫存警示並寄送到貨通知；失敗不影響訂單與庫存操作。
 * 另外每小時全面檢查一次，補上漏掉的情況（例如直接修改資料庫）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final InventoryManagementService inventoryManagementService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onStockChanged(StockChangedEvent event) {
        try {
            inventoryManagementService.checkProductAlerts(event.productIds());
            if (event.replenished()) {
                event.productIds().forEach(inventoryManagementService::processStockNotifications);
            }
        } catch (Exception e) {
            log.error("Failed to process stock change for products {}", event.productIds(), e);
        }
    }

    @Scheduled(cron = "${app.inventory.alert-check-cron:0 0 * * * *}")
    public void hourlyCheck() {
        try {
            inventoryManagementService.checkInventoryAndCreateAlerts();
        } catch (Exception e) {
            log.error("Scheduled inventory alert check failed", e);
        }
    }
}
