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

    /**
     * 生成訂單編號
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int)(Math.random() * 10000));
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
        return dto;
    }

    /**
     * 轉換訂單項目 DTO 到 Entity
     */
    private OrderItem convertItemToEntity(OrderItemDTO dto, Long orderId) {
        OrderItem item = new OrderItem();
        BeanUtils.copyProperties(dto, item);
        item.setOrderId(orderId);
        
        // 計算小計
        BigDecimal subtotal = dto.getUnitPrice().multiply(new BigDecimal(dto.getQuantity()));
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
        order.setOrderNumber(orderNumber);
        order.setStatus(dto.getStatus() != null ? dto.getStatus() : OrderStatus.PENDING_PAYMENT);
        order.setIsDraft(dto.getIsDraft() != null ? dto.getIsDraft() : false);
        
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
        
        return convertToDTO(order);
    }
}
