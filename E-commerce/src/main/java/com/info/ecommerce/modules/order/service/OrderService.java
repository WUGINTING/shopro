package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.OrderItemDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderItem;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.CustomerBlacklistRepository;
import com.info.ecommerce.modules.order.repository.OrderItemRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.order.entity.OrderItem;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import com.info.ecommerce.modules.crm.service.MemberService;
import com.info.ecommerce.modules.product.service.InventoryManagementService;
import lombok.RequiredArgsConstructor;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerBlacklistRepository customerBlacklistRepository;
    private final OrderHistoryService orderHistoryService;
    private final ProductRepository productRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final MemberService memberService;
    private final AdminNotificationService adminNotificationService;

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
     * 檢查客戶是否在黑名單
     */
    private void checkCustomerBlacklist(Long customerId) {
        if (customerBlacklistRepository.existsByCustomerIdAndIsActive(customerId, true)) {
            throw new BusinessException("此客戶已被加入黑名單，無法建立訂單");
        }
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

        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
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
            if (dto.getUnitPrice() == null && spec.getPrice() != null) {
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

        // 如果訂單創建時狀態就是已付款或已完成，更新客戶總消費
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.PAID) {
            try {
                memberService.addTotalSpent(order.getCustomerId(), order.getTotalAmount());
            } catch (Exception e) {
                // 記錄錯誤但不影響訂單創建
                System.err.println("Failed to update member total spent on order creation: " + e.getMessage());
            }
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
        order.setStatus(dto.getStatus());
        order.setPickupType(dto.getPickupType());
        order.setStoreId(dto.getStoreId());
        order.setShippingAddress(dto.getShippingAddress());
        order.setNotes(dto.getNotes());
        order.setDiscountAmount(dto.getDiscountAmount());
        order.setShippingFee(dto.getShippingFee());

        if (dto.getStatus() == OrderStatus.COMPLETED && order.getCompletedAt() == null) {
            order.setCompletedAt(LocalDateTime.now());
        }

        // 更新訂單項目（如果有提供）
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            orderItemRepository.deleteByOrderId(id);
            List<OrderItem> items = dto.getItems().stream()
                .map(itemDto -> convertItemToEntity(itemDto, id))
                .collect(Collectors.toList());
            orderItemRepository.saveAll(items);

            // 重新計算金額
            calculateOrderAmounts(order, items);
        }

        order = orderRepository.save(order);

        // 記錄歷史
        if (oldStatus != dto.getStatus()) {
            orderHistoryService.recordHistory(id, "UPDATE_STATUS", "訂單狀態已更新",
                oldStatus.name(), dto.getStatus().name(), null, null);
            
            // 當訂單狀態變更為已完成或已付款時，更新客戶總消費
            if ((dto.getStatus() == OrderStatus.COMPLETED || dto.getStatus() == OrderStatus.PAID) 
                && (oldStatus != OrderStatus.COMPLETED && oldStatus != OrderStatus.PAID)) {
                try {
                    memberService.addTotalSpent(order.getCustomerId(), order.getTotalAmount());
                } catch (Exception e) {
                    // 記錄錯誤但不影響訂單更新
                    System.err.println("Failed to update member total spent: " + e.getMessage());
                }
            }
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

        // 刪除訂單項目
        orderItemRepository.deleteByOrderId(id);

        // 刪除訂單
        orderRepository.delete(order);

        // 記錄歷史
        orderHistoryService.recordHistory(id, "DELETE", "訂單已刪除",
            order.getStatus().name(), null, null, null);
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
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED && order.getCompletedAt() == null) {
            order.setCompletedAt(LocalDateTime.now());
        }

        order = orderRepository.save(order);

        // 記錄歷史
        orderHistoryService.recordHistory(id, "UPDATE_STATUS", "訂單狀態已更新",
            oldStatus.name(), newStatus.name(), operatorId, operatorName);

        // 當訂單狀態變更為已完成或已付款時，更新客戶總消費
        if ((newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.PAID) 
            && (oldStatus != OrderStatus.COMPLETED && oldStatus != OrderStatus.PAID)) {
            try {
                memberService.addTotalSpent(order.getCustomerId(), order.getTotalAmount());
            } catch (Exception e) {
                // 記錄錯誤但不影響訂單更新
                System.err.println("Failed to update member total spent: " + e.getMessage());
            }
        }

        return convertToDTO(order);
    }
}
