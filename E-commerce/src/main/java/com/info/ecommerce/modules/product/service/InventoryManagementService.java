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
import com.info.ecommerce.modules.system.enums.AdminNotificationType;
import com.info.ecommerce.modules.system.service.AdminNotificationService;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.event.StockChangedEvent;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 庫存管理服務
 * 支持庫存警示、貨到通知等功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryManagementService {

    private final ProductInventoryRepository inventoryRepository;
    private final InventoryAlertRepository alertRepository;
    private final InventoryMovementLogRepository movementLogRepository;
    private final StockNotificationRepository notificationRepository;
    private final AdminNotificationService adminNotificationService;
    private final ProductSpecificationRepository specificationRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.inventory.low-stock-threshold:5}")
    private int lowStockThreshold;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.store-name:遇日小舖}")
    private String storeName;

    @Value("${app.storefront-url:}")
    private String storefrontUrl;

    /**
     * 全面檢查所有商品庫存並建立 / 解除警示
     */
    @Transactional
    public void checkInventoryAndCreateAlerts() {
        java.util.Set<Long> productIds = new java.util.HashSet<>();
        inventoryRepository.findAll().forEach(inventory -> productIds.add(inventory.getProductId()));
        specificationRepository.findAll().forEach(spec -> productIds.add(spec.getProductId()));
        checkProductAlerts(productIds);
    }

    /**
     * 檢查指定商品的庫存警示：
     * - 有規格的商品依各規格庫存判斷（門檻 app.inventory.low-stock-threshold，預設 5）
     * - 無規格的商品依商品庫存與安全庫存判斷
     * - 未追蹤庫存（null）不產生警示；庫存回升後自動解除
     */
    @Transactional
    public void checkProductAlerts(java.util.Collection<Long> productIds) {
        for (Long productId : productIds) {
            if (productId == null) {
                continue;
            }
            List<ProductSpecification> specs = specificationRepository.findByProductId(productId).stream()
                    .filter(spec -> !Boolean.FALSE.equals(spec.getEnabled()))
                    .toList();
            if (!specs.isEmpty()) {
                for (ProductSpecification spec : specs) {
                    evaluate(productId, spec.getId(), spec.getSpecName(), spec.getStock(), lowStockThreshold);
                }
            } else {
                inventoryRepository.findByProductIdAndSpecificationId(productId, null).ifPresent(inventory ->
                        evaluate(productId, null, null, inventory.getAvailableStock(),
                                inventory.getSafetyStock() != null && inventory.getSafetyStock() > 0
                                        ? inventory.getSafetyStock() : lowStockThreshold));
            }
        }
    }

    private void evaluate(Long productId, Long specificationId, String specName, Integer stock, int safetyStock) {
        List<InventoryAlert> existing = alertRepository.findByProductIdAndResolvedFalse(productId).stream()
                .filter(alert -> java.util.Objects.equals(alert.getSpecificationId(), specificationId))
                .toList();
        AlertLevel level = stock == null ? null
                : stock <= 0 ? AlertLevel.OUT_OF_STOCK
                : stock <= safetyStock * 0.5 ? AlertLevel.CRITICAL
                : stock <= safetyStock ? AlertLevel.LOW
                : null;

        if (level == null) {
            existing.forEach(alert -> {
                alert.setResolved(true);
                alert.setResolvedAt(LocalDateTime.now());
            });
            alertRepository.saveAll(existing);
            return;
        }

        String name = productRepository.findById(productId).map(product -> product.getName()).orElse("商品 #" + productId)
                + (specName != null ? "（" + specName + "）" : "");
        String message = switch (level) {
            case OUT_OF_STOCK -> name + " 已售完";
            case CRITICAL -> name + " 庫存嚴重不足，剩餘 " + stock + " 件";
            case LOW -> name + " 庫存偏低，剩餘 " + stock + " 件（安全庫存 " + safetyStock + "）";
        };
        if (!existing.isEmpty()) {
            InventoryAlert alert = existing.get(0);
            boolean worse = level.ordinal() > alert.getAlertLevel().ordinal();
            alert.setAlertLevel(level);
            alert.setCurrentStock(stock);
            alert.setMessage(message);
            alertRepository.save(alert);
            if (worse && level == AlertLevel.OUT_OF_STOCK) {
                adminNotificationService.createNotification(AdminNotificationType.STOCK_LOW, null, productId, "商品已售完", message);
            }
            return;
        }
        alertRepository.save(InventoryAlert.builder()
                .productId(productId)
                .specificationId(specificationId)
                .alertLevel(level)
                .currentStock(stock)
                .safetyStock(safetyStock)
                .message(message)
                .resolved(false)
                .build());
        adminNotificationService.createNotification(AdminNotificationType.STOCK_LOW, null, productId,
                level == AlertLevel.OUT_OF_STOCK ? "商品已售完" : "庫存不足", message);
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
        if (email == null || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BusinessException("請輸入正確的 Email");
        }
        if (specificationId != null && specificationRepository.findById(specificationId)
                .filter(spec -> productId.equals(spec.getProductId()))
                .isEmpty()) {
            throw new BusinessException("商品規格不存在");
        }
        email = email.trim();
        final String normalizedEmail = email;
        boolean exists = notificationRepository.findByProductIdAndNotifiedFalse(productId).stream()
                .anyMatch(item -> normalizedEmail.equalsIgnoreCase(item.getUserEmail())
                        && java.util.Objects.equals(item.getSpecificationId(), specificationId));
        if (exists) {
            return;
        }
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
     * 到貨通知：商品（或指定規格）有庫存時寄信給訂閱者；未設定寄信時保留訂閱，等可寄信後再通知
     */
    @Transactional
    public void processStockNotifications(Long productId) {
        List<StockNotification> notifications =
                notificationRepository.findByProductIdAndNotifiedFalse(productId);
        if (notifications.isEmpty()) {
            return;
        }
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            return;
        }
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return;
        }
        for (StockNotification notification : notifications) {
            if (!isAvailable(productId, notification.getSpecificationId())) {
                continue;
            }
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
                if (mailFrom != null && !mailFrom.isBlank()) {
                    helper.setFrom(mailFrom, storeName);
                }
                helper.setTo(notification.getUserEmail());
                helper.setSubject("【" + storeName + "】您關注的商品已到貨：" + product.getName());
                String link = storefrontUrl == null || storefrontUrl.isBlank() ? ""
                        : "\n\n立即選購：" + storefrontUrl.replaceAll("/+$", "") + "/shop/product/" + productId;
                helper.setText("您好：\n\n您登記到貨通知的商品「" + product.getName() + "」已經補貨，數量有限，歡迎盡快選購。"
                        + link + "\n\n" + storeName + " 敬上\n（此信件由系統自動發送，您只會收到這一次通知）\n", false);
                mailSender.send(message);
                notification.setNotified(true);
                notification.setNotifiedAt(LocalDateTime.now());
            } catch (Exception e) {
                log.warn("Failed to send restock notification {}: {}", notification.getId(), e.getMessage());
            }
        }
        notificationRepository.saveAll(notifications);
    }

    /** 商品或規格目前可購買（未追蹤庫存視為可購買） */
    private boolean isAvailable(Long productId, Long specificationId) {
        if (specificationId != null) {
            return specificationRepository.findById(specificationId)
                    .map(spec -> !Boolean.FALSE.equals(spec.getEnabled()) && (spec.getStock() == null || spec.getStock() > 0))
                    .orElse(false);
        }
        List<ProductSpecification> specs = specificationRepository.findByProductId(productId).stream()
                .filter(spec -> !Boolean.FALSE.equals(spec.getEnabled()))
                .toList();
        if (!specs.isEmpty()) {
            return specs.stream().anyMatch(spec -> spec.getStock() == null || spec.getStock() > 0);
        }
        return inventoryRepository.findByProductIdAndSpecificationId(productId, null)
                .map(inventory -> inventory.getAvailableStock() == null || inventory.getAvailableStock() > 0)
                .orElse(true);
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
        ProductSpecification spec = specificationId == null ? null : specificationRepository.findById(specificationId)
                .filter(item -> productId.equals(item.getProductId()))
                .orElseThrow(() -> new BusinessException("規格不存在"));
        if (quantity < 0) {
            // 未追蹤庫存（null）沒有可扣的數量；追蹤中的庫存不可扣成負數
            Integer tracked = spec != null ? spec.getStock()
                    : inventory != null ? inventory.getAvailableStock() : null;
            if (tracked == null) {
                throw new BusinessException("此商品未追蹤庫存，請先以入庫設定庫存數量");
            }
            if (tracked + quantity < 0) {
                throw new BusinessException("調整後庫存不可小於 0（目前庫存 " + tracked + "）");
            }
        }
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

        // 有規格的商品，結帳以規格庫存為準：同步調整規格庫存
        if (spec != null) {
            int specBefore = spec.getStock() != null ? spec.getStock() : 0;
            spec.setStock(Math.max(specBefore + quantity, 0));
            specificationRepository.save(spec);
            beforeStock = specBefore;
            afterStock = spec.getStock();
        }
        saveInventoryMovementLog(productId, specificationId, inventory.getWarehouseId(),
                quantity, beforeStock, afterStock, source, remark);

        eventPublisher.publishEvent(new StockChangedEvent(List.of(productId), quantity > 0));
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
