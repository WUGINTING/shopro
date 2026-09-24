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

    /**
     * 扣除訂單品項庫存；任一品項庫存不足時丟出 BusinessException（呼叫端交易會整筆回滾）
     */
    @Transactional
    public void reserve(Long orderId, String orderNumber) {
        if (orderHistoryRepository.existsByOrderIdAndActionType(orderId, ACTION_RESERVED)) {
            return;
        }
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
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
        orderHistoryService.recordHistory(orderId, ACTION_RESERVED, "已扣除訂單商品庫存", null, null, null, "系統");
    }

    /**
     * 歸還訂單庫存（僅限曾扣過庫存且尚未歸還的訂單）
     */
    @Transactional
    public void release(Long orderId, String orderNumber) {
        if (!orderHistoryRepository.existsByOrderIdAndActionType(orderId, ACTION_RESERVED)
                || orderHistoryRepository.existsByOrderIdAndActionType(orderId, ACTION_RELEASED)) {
            return;
        }
        for (OrderItem item : orderItemRepository.findByOrderId(orderId)) {
            int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            if (quantity <= 0) {
                continue;
            }
            if (item.getSpecificationId() != null) {
                if (productSpecificationRepository.incrementStock(item.getSpecificationId(), quantity) > 0) {
                    productInventoryRepository.adjustSpecificationStock(item.getProductId(), item.getSpecificationId(), quantity);
                    logMovement(item, quantity, specificationStock(item), "訂單 " + orderNumber + " 取消，歸還庫存");
                }
            } else if (productInventoryRepository.incrementProductLevelStock(item.getProductId(), quantity) > 0) {
                logMovement(item, quantity, productLevelStock(item), "訂單 " + orderNumber + " 取消，歸還庫存");
            }
        }
        orderHistoryService.recordHistory(orderId, ACTION_RELEASED, "訂單取消，已歸還商品庫存", null, null, null, "系統");
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
