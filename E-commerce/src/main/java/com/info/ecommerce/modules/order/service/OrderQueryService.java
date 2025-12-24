package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.OrderQueryDTO;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderItem;
import com.info.ecommerce.modules.order.repository.OrderItemRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 訂單查詢服務 - 進階查詢功能
 */
@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 多條件查詢訂單
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> searchOrders(OrderQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(
            queryDTO.getPage() != null ? queryDTO.getPage() : 0,
            queryDTO.getSize() != null ? queryDTO.getSize() : 20
        );
        
        Page<Order> orders;
        
        if (queryDTO.getOrderNumber() != null && !queryDTO.getOrderNumber().isEmpty()) {
            // 根據訂單編號查詢
            Order order = orderRepository.findByOrderNumber(queryDTO.getOrderNumber())
                .orElseThrow(() -> new BusinessException("訂單不存在"));
            OrderDTO dto = convertToDTO(order);
            return new org.springframework.data.domain.PageImpl<>(
                List.of(dto), pageable, 1);
        } else {
            // 多條件查詢
            orders = orderRepository.findByMultipleCriteria(
                queryDTO.getCustomerId(),
                queryDTO.getStatus(),
                queryDTO.getIsDraft(),
                queryDTO.getStoreId(),
                queryDTO.getStartDate(),
                queryDTO.getEndDate(),
                queryDTO.getMinAmount(),
                queryDTO.getMaxAmount(),
                pageable
            );
        }
        
        return orders.map(this::convertToDTO);
    }

    /**
     * 根據訂單編號查詢
     */
    @Transactional(readOnly = true)
    public OrderDTO findByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new BusinessException("訂單不存在"));
        return convertToDTO(order);
    }

    /**
     * 根據客戶姓名模糊查詢
     * 注意：為避免性能問題，此方法應該在 Repository 層實作
     * 這裡提供簡化實作，實際應用應使用 JPQL 或 Specification
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> searchByCustomerName(String customerName) {
        // TODO: 應該在 OrderRepository 中實作專用的查詢方法
        // 例如: Page<Order> findByCustomerNameContaining(String name, Pageable pageable)
        // 當前實作僅適用於小規模資料集
        return orderRepository.findAll().stream()
            .filter(order -> order.getCustomerName() != null && 
                           order.getCustomerName().contains(customerName))
            .limit(100)  // 限制結果數量避免記憶體問題
            .map(this::convertToDTO)
            .collect(Collectors.toList());
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
     * 轉換訂單項目到 DTO
     */
    private com.info.ecommerce.modules.order.dto.OrderItemDTO convertItemToDTO(OrderItem item) {
        com.info.ecommerce.modules.order.dto.OrderItemDTO dto = 
            new com.info.ecommerce.modules.order.dto.OrderItemDTO();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }
}
