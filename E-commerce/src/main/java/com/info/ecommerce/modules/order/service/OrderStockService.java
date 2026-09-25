package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.entity.OrderItem;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import com.info.ecommerce.modules.order.repository.OrderItemRepository;
import com.info.ecommerce.modules.product.entity.InventoryMovementLog;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.repository.InventoryMovementLogRepository;
import com.info.ecommerce.modules.product.repository.ProductInventoryRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 訂單庫存：下單時扣庫存、取消時歸還。
 * <ul>
 *   <li>有規格的品項以規格庫存（product_specifications.stock）為準；若有對應的倉儲記錄一併同步。</li>
 *   <li>無規格的品項以商品層級倉儲庫存（product_inventory，specification_id 為空）為準。</li>
 *   <li>庫存欄位為空或無倉儲記錄視為「不追蹤庫存」，不限制購買數量。</li>
 * </ul>
 * 扣減使用條件式 UPDATE（stock >= 數量），同時下單也不會超賣。
 * 透過訂單歷程的 STOCK_RESERVED / STOCK_RELEASED 記錄確保每筆訂單只扣一次、只還一次。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStockService {

    public static final String ACTION_RESERVED = "STOCK_RESERVED";
    public static final String ACTION_RELEASED = "STOCK_RELEASED";

    private final ProductSpecificationRepository productSpecificationRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final InventoryMovementLogRepository movementLogRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderHistoryService orderHistoryService;
    private final OrderCouponService orderCouponService;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    /**
     * 扣除訂單品項庫存；任一品項庫存不足時丟出 BusinessException（呼叫端交易會整筆回滾）
     */
    @Transactional
    public void reserve(Long orderId, String orderNumber) {
        if (reservedCount(orderId) > 0) {
            return;
        }
        reserveItems(orderItemRepository.findByOrderId(orderId), orderNumber);
        orderHistoryService.recordHistory(orderId, ACTION_RESERVED, "已扣除訂單商品庫存", null, null, null, "系統");
    }

    /**
     * 歸還訂單庫存（僅限曾扣過庫存且尚未歸還的訂單）
     */
    @Transactional
    public void release(Long orderId, String orderNumber) {
        orderCouponService.release(orderId);
        if (!holdsStock(orderId)) {
            return;
        }
        returnItems(orderItemRepository.findByOrderId(orderId), "訂單 " + orderNumber + " 取消，歸還庫存");
        orderHistoryService.recordHistory(orderId, ACTION_RELEASED, "訂單取消，已歸還商品庫存", null, null, null, "系統");
    }

    /**
     * 訂單品項被修改時調整庫存：歸還舊品項、扣除新品項（庫存不足時丟出例外，呼叫端交易整筆回滾）。
     * 只對目前仍佔用庫存的訂單（已扣且未歸還）生效。
     */
    @Transactional
    public void replaceItems(Long orderId, String orderNumber, List<OrderItem> oldItems, List<OrderItem> newItems) {
        if (!holdsStock(orderId)) {
            return;
        }
        returnItems(oldItems, "訂單 " + orderNumber + " 修改品項，歸還原品項庫存");
        reserveItems(newItems, orderNumber);
        orderHistoryService.recordHistory(orderId, "STOCK_ADJUSTED", "訂單品項修改，已重新計算庫存", null, null, null, "系統");
    }

    /**
     * 已取消的前台訂單被恢復（改回待付款、已付款等）時重新扣庫存；庫存不足時丟出例外，狀態變更整筆回滾。
     * 從未扣過庫存的訂單（後台建立）不處理。
     */
    @Transactional
    public void reserveAgain(Long orderId, String orderNumber) {
        orderCouponService.reclaim(orderId);
        long reserved = reservedCount(orderId);
        if (reserved == 0 || reserved > releasedCount(orderId)) {
            return;
        }
        reserveItems(orderItemRepository.findByOrderId(orderId), orderNumber);
        orderHistoryService.recordHistory(orderId, ACTION_RESERVED, "訂單恢復，已重新扣除商品庫存", null, null, null, "系統");
    }

    /** 訂單目前是否佔用庫存（扣除次數多於歸還次數） */
    public boolean holdsStock(Long orderId) {
        return reservedCount(orderId) > releasedCount(orderId);
    }

    private long reservedCount(Long orderId) {
        return orderHistoryRepository.countByOrderIdAndActionType(orderId, ACTION_RESERVED);
    }

    private long releasedCount(Long orderId) {
        return orderHistoryRepository.countByOrderIdAndActionType(orderId, ACTION_RELEASED);
    }

    private void reserveItems(List<OrderItem> items, String orderNumber) {
        publishStockChanged(items, false);
        for (OrderItem item : items) {
            int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            if (quantity <= 0) {
                continue;
            }
            if (item.getSpecificationId() != null) {
                reserveSpecification(item, quantity, orderNumber);
            } else {
                reserveProductLevel(item, quantity, orderNumber);
            }
        }
    }

    /** 交易提交後更新低庫存警示；歸還庫存時一併檢查到貨通知 */
    private void publishStockChanged(List<OrderItem> items, boolean replenished) {
        java.util.Set<Long> productIds = items.stream()
                .map(OrderItem::getProductId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        if (!productIds.isEmpty()) {
            eventPublisher.publishEvent(new com.info.ecommerce.modules.product.event.StockChangedEvent(productIds, replenished));
        }
    }

    private void returnItems(List<OrderItem> items, String remark) {
        publishStockChanged(items, true);
        for (OrderItem item : items) {
            int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            if (quantity <= 0) {
                continue;
            }
            if (item.getSpecificationId() != null) {
                if (productSpecificationRepository.incrementStock(item.getSpecificationId(), quantity) > 0) {
                    productInventoryRepository.adjustSpecificationStock(item.getProductId(), item.getSpecificationId(), quantity);
                    logMovement(item, quantity, specificationStock(item), remark);
                }
            } else if (productInventoryRepository.incrementProductLevelStock(item.getProductId(), quantity) > 0) {
                logMovement(item, quantity, productLevelStock(item), remark);
            }
        }
    }

    private void reserveSpecification(OrderItem item, int quantity, String orderNumber) {
        if (productSpecificationRepository.decrementStock(item.getSpecificationId(), quantity) > 0) {
            productInventoryRepository.adjustSpecificationStock(item.getProductId(), item.getSpecificationId(), -quantity);
            logMovement(item, -quantity, specificationStock(item), "訂單 " + orderNumber + " 扣庫存");
            return;
        }
        ProductSpecification spec = productSpecificationRepository.findById(item.getSpecificationId())
                .orElseThrow(() -> new BusinessException("商品規格不存在"));
        if (spec.getStock() == null) {
            return; // 未追蹤庫存
        }
        throw new BusinessException("商品「" + item.getProductName() + " (" + spec.getSpecName() + ")」庫存不足，目前剩餘 "
                + Math.max(spec.getStock(), 0) + " 件");
    }

    private void reserveProductLevel(OrderItem item, int quantity, String orderNumber) {
        if (productInventoryRepository.decrementProductLevelStock(item.getProductId(), quantity) > 0) {
            logMovement(item, -quantity, productLevelStock(item), "訂單 " + orderNumber + " 扣庫存");
            return;
        }
        productInventoryRepository.findByProductIdAndSpecificationId(item.getProductId(), null)
                .filter(inventory -> inventory.getAvailableStock() != null)
                .ifPresent(inventory -> {
                    throw new BusinessException("商品「" + item.getProductName() + "」庫存不足，目前剩餘 "
                            + Math.max(inventory.getAvailableStock(), 0) + " 件");
                });
        // 無倉儲記錄：未追蹤庫存
    }

    private int specificationStock(OrderItem item) {
        return productSpecificationRepository.findById(item.getSpecificationId())
                .map(ProductSpecification::getStock)
                .orElse(0);
    }

    private int productLevelStock(OrderItem item) {
        return productInventoryRepository.findByProductIdAndSpecificationId(item.getProductId(), null)
                .map(inventory -> inventory.getAvailableStock() != null ? inventory.getAvailableStock() : 0)
                .orElse(0);
    }

    private void logMovement(OrderItem item, int change, int afterStock, String remark) {
        movementLogRepository.save(InventoryMovementLog.builder()
                .productId(item.getProductId())
                .specificationId(item.getSpecificationId())
                .changeType(change > 0 ? "INCREASE" : "DECREASE")
                .source("ORDER")
                .changeQuantity(change)
                .beforeStock(afterStock - change)
                .afterStock(afterStock)
                .remark(remark)
                .build());
    }
}
