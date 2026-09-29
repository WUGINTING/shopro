package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.event.OrderEmailEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.OrderItemDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderItem;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.CustomerBlacklistRepository;
import com.info.ecommerce.modules.order.repository.OrderDiscountRepository;
import com.info.ecommerce.modules.order.repository.OrderItemRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import com.info.ecommerce.modules.product.service.InventoryManagementService;
import com.info.ecommerce.modules.crm.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.info.ecommerce.modules.system.enums.AdminNotificationType;
import com.info.ecommerce.modules.system.service.AdminNotificationService;

/**
 * 訂單服務 - 基礎 CRUD 操作
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDiscountRepository orderDiscountRepository;
    private final com.info.ecommerce.modules.order.repository.OrderHistoryRepository orderHistoryRepository;
    private final com.info.ecommerce.modules.order.repository.OrderPaymentRepository orderPaymentRepository;
    private final CustomerBlacklistRepository customerBlacklistRepository;
    private final OrderHistoryService orderHistoryService;
    private final ProductRepository productRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final MemberService memberService;
    private final AdminNotificationService adminNotificationService;
    private final OrderStockService orderStockService;
    private final ApplicationEventPublisher eventPublisher;
    private final com.info.ecommerce.modules.order.repository.OrderShipmentRepository orderShipmentRepository;

    /**
     * 生成訂單編號
     * 格式：ORD + yyyyMMddHHmm + xxxx (總共19字元，符合ECPay的20字元限制)
     * 注意：ECPay 要求 MerchantTradeNo 最多20字元
     */
    private String generateOrderNumber() {
        // 使用 yyyyMMddHHmm (12位) 而不是 yyyyMMddHHmmss (14位) 以符合ECPay的20字元限制
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String random = String.format("%04d", (int)(Math.random() * 10000));
        // 總長度：3(ORD) + 12(時間戳) + 4(隨機數) = 19字元
        return "ORD" + timestamp + random;
    }

    /**
     * 訂單是否曾收款：有成功的付款紀錄（線上付款），或曾被標記為已付款（例如貨到付款收款）
     */
    /** 會在 new_status 記錄訂單 / 付款狀態 PAID 的歷程動作 */
    private static final java.util.List<String> PAID_STATUS_ACTIONS = java.util.List.of(
        "CREATE", "UPDATE", "UPDATE_STATUS", "BATCH_UPDATE_STATUS", "PAYMENT_SUCCESS", "CREATE_PAYMENT", "UPDATE_PAYMENT_STATUS");

    @Transactional(readOnly = true)
    public boolean hasBeenPaid(Long orderId) {
        // new_status 欄位在其他動作中也存放優惠券代碼、退款金額等資料，只看狀態變更類的記錄
        return orderRepository.findById(orderId).map(order -> order.getStatus() == OrderStatus.PAID).orElse(false)
            || orderHistoryRepository.existsByOrderIdAndNewStatusAndActionTypeIn(orderId, OrderStatus.PAID.name(), PAID_STATUS_ACTIONS)
            || orderPaymentRepository.findByOrderId(orderId).stream()
                .anyMatch(payment -> payment.getPaymentStatus() == com.info.ecommerce.modules.order.enums.PaymentStatus.PAID
                    || payment.getPaymentStatus() == com.info.ecommerce.modules.order.enums.PaymentStatus.REFUNDING
                    || payment.getPaymentStatus() == com.info.ecommerce.modules.order.enums.PaymentStatus.REFUNDED);
    }

    /** 已收款的訂單不可直接取消（處理中的線上付款訂單也一樣），必須走退款 */
    private void assertCancellable(Long orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED && hasBeenPaid(orderId)) {
            throw new BusinessException("此訂單已收款，不可直接取消，請使用「登記退款」");
        }
    }

    /**
     * 取消仍未付款的訂單（逾期未付款清理使用）。先鎖定訂單列並重新確認狀態，
     * 與付款回呼互斥，避免在付款成功的同時把訂單取消。
     *
     * @return 是否確實取消
     */
    @Transactional
    public boolean cancelIfStillUnpaid(Long id, String operatorName) {
        Order locked = orderRepository.findByIdForUpdate(id).orElse(null);
        if (locked == null || locked.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return false;
        }
        updateOrderStatus(id, OrderStatus.CANCELLED, null, operatorName);
        return true;
    }

    /**
     * 狀態變為已付款或已取消時通知顧客（交易提交後寄送）
     */
    private void publishStatusEmail(Long orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        if (newStatus == null || newStatus == oldStatus) {
            return;
        }
        if (newStatus == OrderStatus.PAID) {
            eventPublisher.publishEvent(new OrderEmailEvent(orderId, OrderEmailEvent.Type.PAID));
        } else if (newStatus == OrderStatus.CANCELLED) {
            eventPublisher.publishEvent(new OrderEmailEvent(orderId, OrderEmailEvent.Type.CANCELLED));
        } else if (newStatus == OrderStatus.REFUNDED) {
            eventPublisher.publishEvent(new OrderEmailEvent(orderId, OrderEmailEvent.Type.REFUNDED));
        }
    }

    /**
     * 檢查客戶是否在黑名單
     */
    private void checkCustomerBlacklist(Long customerId) {
        if (customerBlacklistRepository.existsByCustomerIdAndIsActive(customerId, true)) {
            throw new BusinessException("此客戶已被加入黑名單，無法建立訂單");
        }
    }

    private static boolean sameAmount(BigDecimal a, BigDecimal b) {
        BigDecimal left = a == null ? BigDecimal.ZERO : a;
        BigDecimal right = b == null ? BigDecimal.ZERO : b;
        return left.compareTo(right) == 0;
    }

    /** 品項是否有實質變更（商品、規格、數量、單價） */
    private static boolean itemsDiffer(List<OrderItem> current, List<OrderItemDTO> requested) {
        if (current.size() != requested.size()) {
            return true;
        }
        java.util.function.Function<Object[], String> key = parts -> java.util.Arrays.toString(parts);
        List<String> currentKeys = current.stream()
            .map(item -> key.apply(new Object[]{item.getProductId(), item.getSpecificationId(), item.getQuantity(),
                item.getUnitPrice() == null ? null : item.getUnitPrice().stripTrailingZeros().toPlainString()}))
            .sorted().collect(Collectors.toList());
        List<String> requestedKeys = requested.stream()
            .map(item -> key.apply(new Object[]{item.getProductId(), item.getSpecificationId(), item.getQuantity(),
                item.getUnitPrice() == null ? null : item.getUnitPrice().stripTrailingZeros().toPlainString()}))
            .sorted().collect(Collectors.toList());
        return !currentKeys.equals(requestedKeys);
    }

    /**
     * 計算訂單金額
     */
    private void calculateOrderAmounts(Order order, List<OrderItem> items) {
        // 使用項目的小計計算訂單小計（避免重複套用折扣）
        BigDecimal subtotal = items.stream()
            .map(OrderItem::getSubtotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setSubtotalAmount(subtotal);

        // 折扣不可超過商品小計（例如修改品項後小計變少），避免總額變成負數
        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount().max(BigDecimal.ZERO) : BigDecimal.ZERO;
        discount = discount.min(subtotal);
        order.setDiscountAmount(discount);
        BigDecimal shipping = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;

        // 總金額 = 小計 - 訂單折扣 + 運費
        order.setTotalAmount(subtotal.subtract(discount).add(shipping));
    }

    /**
     * 轉換 DTO 到 Entity
     */
    private Order convertToEntity(OrderDTO dto) {
        Order order = new Order();
        BeanUtils.copyProperties(dto, order);
        return order;
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);

        // 加載訂單項目
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        dto.setItems(items.stream().map(this::convertItemToDTO).collect(Collectors.toList()));

        return dto;
    }

    /**
     * 轉換訂單項目 Entity 到 DTO
     */
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(item, dto);
        // 確保所有字段都被正確複製（手動設置以確保字段映射正確）
        dto.setId(item.getId());
        dto.setOrderId(item.getOrderId());
        dto.setProductId(item.getProductId());
        dto.setSpecificationId(item.getSpecificationId());
        dto.setProductName(item.getProductName());
        dto.setProductSku(item.getProductSku());
        dto.setProductSpec(item.getProductSpec());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotalAmount(item.getSubtotalAmount());
        dto.setDiscountAmount(item.getDiscountAmount());
        dto.setActualAmount(item.getActualAmount());
        return dto;
    }

    /**
     * 轉換訂單項目 DTO 到 Entity
     */
    private OrderItem convertItemToEntity(OrderItemDTO dto, Long orderId) {
        OrderItem item = new OrderItem();
        BeanUtils.copyProperties(dto, item);
        item.setOrderId(orderId);
        item.setId(null);

        // 確保 specificationId 被正確設置
        if (dto.getSpecificationId() != null) {
            item.setSpecificationId(dto.getSpecificationId());

            // 從規格中獲取信息
            ProductSpecification spec = productSpecificationRepository.findById(dto.getSpecificationId())
                    .orElseThrow(() -> new BusinessException("商品規格不存在: " + dto.getSpecificationId()));

            // 驗證規格是否屬於該商品
            if (!spec.getProductId().equals(dto.getProductId())) {
                throw new BusinessException("商品規格不屬於該商品");
            }

            // 從規格中獲取信息
            if (spec.getSku() != null && !spec.getSku().isEmpty()) {
                item.setProductSku(spec.getSku());
            }
            if (spec.getSpecName() != null && !spec.getSpecName().isEmpty()) {
                item.setProductSpec(spec.getSpecName());
            }
            // 如果DTO中沒有提供單價，使用規格的價格
            if (dto.getUnitPrice() == null && spec.getPrice() != null && spec.getPrice().signum() > 0) {
                item.setUnitPrice(spec.getPrice());
            }
        }

        // 獲取商品信息
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new BusinessException("商品不存在: " + dto.getProductId()));
        item.setProductName(product.getName());

        // 如果沒有設置SKU，使用商品的SKU
        if (item.getProductSku() == null || item.getProductSku().isEmpty()) {
            if (product.getSku() != null && !product.getSku().isEmpty()) {
                item.setProductSku(product.getSku());
            }
        }

        // 確保單價不為空
        if (item.getUnitPrice() == null) {
            if (dto.getUnitPrice() != null) {
                item.setUnitPrice(dto.getUnitPrice());
            } else if (product.getSalePrice() != null) {
                item.setUnitPrice(product.getSalePrice());
            } else if (product.getBasePrice() != null) {
                item.setUnitPrice(product.getBasePrice());
            } else {
                throw new BusinessException("無法確定商品單價，請提供單價或選擇商品規格");
            }
        }

        // 計算小計
        BigDecimal subtotal = item.getUnitPrice().multiply(new BigDecimal(dto.getQuantity()));
        item.setSubtotalAmount(subtotal);

        // 計算實際金額（小計 - 折扣）
        BigDecimal discount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
        item.setActualAmount(subtotal.subtract(discount));

        return item;
    }

    /**
     * 創建訂單
     */
    @Transactional
    public OrderDTO createOrder(OrderDTO dto) {
        // 檢查黑名單
        checkCustomerBlacklist(dto.getCustomerId());

        // 生成訂單編號
        String orderNumber = generateOrderNumber();
        while (orderRepository.existsByOrderNumber(orderNumber)) {
            orderNumber = generateOrderNumber();
        }

        // 創建訂單
        Order order = convertToEntity(dto);
        order.setId(null);
        order.setOrderNumber(orderNumber);
        order.setStatus(dto.getStatus() != null ? dto.getStatus() : OrderStatus.PENDING_PAYMENT);
        order.setIsDraft(dto.getIsDraft() != null ? dto.getIsDraft() : false);

        // 確保客戶信息被正確設置（即使有 customerId，也保存客戶信息用於顯示）
        if (dto.getCustomerName() != null) {
            order.setCustomerName(dto.getCustomerName());
        }
        if (dto.getCustomerPhone() != null) {
            order.setCustomerPhone(dto.getCustomerPhone());
        }
        if (dto.getCustomerEmail() != null) {
            order.setCustomerEmail(dto.getCustomerEmail());
        }

        // 保存訂單項目
        List<OrderItem> items = dto.getItems() != null ?
            dto.getItems().stream()
                .map(itemDto -> convertItemToEntity(itemDto, null))
                .collect(Collectors.toList()) :
            List.of();

        // 計算訂單金額
        calculateOrderAmounts(order, items);

        // 保存訂單
        order = orderRepository.save(order);

        // 保存訂單項目
        final Long orderId = order.getId();
        items.forEach(item -> item.setOrderId(orderId));
        orderItemRepository.saveAll(items);

        // 記錄歷史
        orderHistoryService.recordHistory(orderId, "CREATE", "訂單已建立", null,
            order.getStatus().name(), null, null);

        // 發送訂單新增通知
        adminNotificationService.createNotification(
            AdminNotificationType.ORDER_CREATED,
            orderId,
            null,
            "新訂單",
            "收到新訂單 #" + order.getOrderNumber() + "，金額：NT$" + order.getTotalAmount()
        );

        // 訂單建立時就是已付款/已完成時，更新客戶累計消費
        if (MemberService.SPENDING_STATUSES.contains(order.getStatus())) {
            syncMemberSpending(order.getCustomerId());
        }

        return convertToDTO(order);
    }

    /**
     * 更新訂單
     */
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new BusinessException("訂單不存在"));

        OrderStatus oldStatus = order.getStatus();

        // 更新訂單基本資料
        if (dto.getCustomerId() != null) {
            order.setCustomerId(dto.getCustomerId());
        }
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerPhone(dto.getCustomerPhone());
        order.setCustomerEmail(dto.getCustomerEmail());
        if (dto.getStatus() != null) {
            OrderStatusRules.assertCanChange(oldStatus, dto.getStatus());
            assertCancellable(id, oldStatus, dto.getStatus());
            order.setStatus(dto.getStatus());
        }
        order.setPickupType(dto.getPickupType());
        order.setStoreId(dto.getStoreId());
        order.setShippingAddress(dto.getShippingAddress());
        order.setNotes(dto.getNotes());

        // 金額相關（品項、折扣、運費）只能在待付款時調整，避免已收款訂單的金額與實收不符
        List<OrderItem> currentItems = orderItemRepository.findByOrderId(id);
        boolean itemsChanged = dto.getItems() != null && !dto.getItems().isEmpty() && itemsDiffer(currentItems, dto.getItems());
        boolean hasDiscountRecords = !orderDiscountRepository.findByOrderId(id).isEmpty();
        BigDecimal newDiscount = hasDiscountRecords ? order.getDiscountAmount() : dto.getDiscountAmount();
        boolean amountsChanged = itemsChanged
            || !sameAmount(newDiscount, order.getDiscountAmount())
            || !sameAmount(dto.getShippingFee(), order.getShippingFee());
        if (amountsChanged && oldStatus != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("只有待付款的訂單可以修改品項、折扣或運費；已付款訂單請改用退款或取消後重新建立");
        }
        // 有「訂單折扣」紀錄時，折扣金額由折扣紀錄決定
        order.setDiscountAmount(newDiscount);
        order.setShippingFee(dto.getShippingFee());

        if (dto.getStatus() == OrderStatus.COMPLETED && order.getCompletedAt() == null) {
            order.setCompletedAt(LocalDateTime.now());
        }

        // 更新訂單項目（有變更時）
        if (itemsChanged) {
            List<OrderItem> oldItems = currentItems;
            orderItemRepository.deleteByOrderId(id);
            List<OrderItem> items = dto.getItems().stream()
                .map(itemDto -> convertItemToEntity(itemDto, id))
                .collect(Collectors.toList());
            orderItemRepository.saveAll(items);

            // 前台訂單已扣庫存：品項變更時同步調整
            orderStockService.replaceItems(id, order.getOrderNumber(), oldItems, items);

            // 重新計算金額
            calculateOrderAmounts(order, items);
        } else if (amountsChanged) {
            calculateOrderAmounts(order, currentItems);
        }

        order = orderRepository.save(order);

        if (dto.getStatus() == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            orderStockService.release(id, order.getOrderNumber());
        } else if (oldStatus == OrderStatus.CANCELLED && dto.getStatus() != null && dto.getStatus() != OrderStatus.CANCELLED) {
            orderStockService.reserveAgain(id, order.getOrderNumber());
        }
        publishStatusEmail(id, oldStatus, dto.getStatus());

        // 記錄歷史
        if (dto.getStatus() != null && oldStatus != dto.getStatus()) {
            orderHistoryService.recordHistory(id, "UPDATE_STATUS", "訂單狀態已更新",
                oldStatus.name(), dto.getStatus().name(), null, null);

            syncMemberSpending(order.getCustomerId());
        } else {
            orderHistoryService.recordHistory(id, "UPDATE", "訂單已更新",
                null, null, null, null);
        }

        return convertToDTO(order);
    }

    /**
     * 取得訂單詳情
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new BusinessException("訂單不存在"));
        return convertToDTO(order);
    }

    /**
     * 刪除訂單
     */
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new BusinessException("訂單不存在"));

        // 尚未出貨的訂單（待付款 / 已付款 / 處理中，且沒有任何已出貨或已送達的物流）刪除前歸還庫存與優惠券；
        // 已出貨 / 已完成的商品已實際賣出，已取消 / 已退款的訂單在當時已處理，都不再歸還（release 可重複呼叫）
        boolean shipped = orderShipmentRepository.findByOrderId(id).stream()
            .anyMatch(shipment -> shipment.getShippingStatus() == com.info.ecommerce.modules.order.enums.ShippingStatus.SHIPPED
                || shipment.getShippingStatus() == com.info.ecommerce.modules.order.enums.ShippingStatus.DELIVERED);
        boolean unfulfilled = !shipped && (order.getStatus() == OrderStatus.PENDING_PAYMENT
            || order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.PROCESSING);
        if (unfulfilled) {
            orderStockService.release(id, order.getOrderNumber());
        }

        // 刪除訂單項目
        orderItemRepository.deleteByOrderId(id);

        // 刪除訂單
        orderRepository.delete(order);

        // 記錄歷史
        orderHistoryService.recordHistory(id, "DELETE", "訂單已刪除",
            order.getStatus().name(), null, null, null);
        // 會員累積消費不再計入已刪除的訂單
        memberService.syncTotalSpent(order.getCustomerId());
    }

    /**
     * 批量刪除訂單
     */
    @Transactional
    public void deleteOrders(List<Long> ids) {
        ids.forEach(this::deleteOrder);
    }

    /**
     * 分頁查詢訂單
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> listOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * 根據客戶 ID 查詢訂單
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> listOrdersByCustomerId(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable).map(this::convertToDTO);
    }

    /**
     * 根據狀態查詢訂單
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> listOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable).map(this::convertToDTO);
    }

    /**
     * 根據草稿狀態查詢訂單
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> listDraftOrders(Boolean isDraft, Pageable pageable) {
        return orderRepository.findByIsDraft(isDraft, pageable).map(this::convertToDTO);
    }

    /**
     * 更新訂單狀態
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus newStatus, Long operatorId, String operatorName) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new BusinessException("訂單不存在"));

        OrderStatus oldStatus = order.getStatus();
        OrderStatusRules.assertCanChange(oldStatus, newStatus);
        assertCancellable(id, oldStatus, newStatus);
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED && order.getCompletedAt() == null) {
            order.setCompletedAt(LocalDateTime.now());
        }

        order = orderRepository.save(order);

        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            orderStockService.release(id, order.getOrderNumber());
        } else if (oldStatus == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
            orderStockService.reserveAgain(id, order.getOrderNumber());
        }
        publishStatusEmail(id, oldStatus, newStatus);

        // 記錄歷史
        orderHistoryService.recordHistory(id, "UPDATE_STATUS", "訂單狀態已更新",
            oldStatus.name(), newStatus.name(), operatorId, operatorName);

        // 發送狀態變更通知
        if (newStatus == OrderStatus.PAID) {
            adminNotificationService.createNotification(
                AdminNotificationType.PAYMENT_COMPLETED,
                id,
                null,
                "收款完成",
                "訂單 #" + order.getOrderNumber() + " 已完成付款，金額：NT$" + order.getTotalAmount()
            );
        } else if (newStatus == OrderStatus.CANCELLED) {
            adminNotificationService.createNotification(
                AdminNotificationType.ORDER_CANCELLED,
                id,
                null,
                "訂單取消",
                "訂單 #" + order.getOrderNumber() + " 已被取消"
            );
        }

        if (oldStatus != newStatus) {
            syncMemberSpending(order.getCustomerId());
        }

        return convertToDTO(order);
    }

    /** 依訂單重新計算會員累計消費；失敗只記錄，不影響訂單操作 */
    private void syncMemberSpending(Long customerId) {
        try {
            memberService.syncTotalSpent(customerId);
        } catch (Exception e) {
            log.warn("Failed to update member total spent for customer {}", customerId, e);
        }
    }
}
