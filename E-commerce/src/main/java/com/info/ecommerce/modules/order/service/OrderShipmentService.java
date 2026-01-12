package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.OrderShipmentDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderShipment;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.ShippingStatus;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.order.repository.OrderShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物流服務 - 物流管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderShipmentService {

    private final OrderShipmentRepository orderShipmentRepository;
    private final OrderHistoryService orderHistoryService;
    private final OrderRepository orderRepository;

    /**
     * 創建物流記錄
     */
    @Transactional
    public OrderShipmentDTO createShipment(OrderShipmentDTO dto) {
        OrderShipment shipment = convertToEntity(dto);
        
        // 如果創建時狀態就是 SHIPPED，設置出貨時間
        if (dto.getShippingStatus() == ShippingStatus.SHIPPED && shipment.getShippedAt() == null) {
            shipment.setShippedAt(LocalDateTime.now());
        }
        
        shipment = orderShipmentRepository.save(shipment);
        
        // 如果創建時狀態就是 SHIPPED，同時更新訂單狀態為 PROCESSING（處理中/已發貨）
        if (dto.getShippingStatus() == ShippingStatus.SHIPPED) {
            updateOrderStatusToProcessing(dto.getOrderId());
        }
        
        // 記錄歷史
        orderHistoryService.recordHistory(dto.getOrderId(), "CREATE_SHIPMENT", 
            "建立物流記錄: " + dto.getShippingCompany(), 
            null, dto.getShippingStatus().name(), null, null);
        
        return convertToDTO(shipment);
    }

    /**
     * 更新物流狀態
     */
    @Transactional
    public OrderShipmentDTO updateShippingStatus(Long shipmentId, ShippingStatus status) {
        OrderShipment shipment = orderShipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new RuntimeException("物流記錄不存在"));
        
        ShippingStatus oldStatus = shipment.getShippingStatus();
        shipment.setShippingStatus(status);
        
        if (status == ShippingStatus.SHIPPED && shipment.getShippedAt() == null) {
            shipment.setShippedAt(LocalDateTime.now());
        } else if (status == ShippingStatus.DELIVERED && shipment.getDeliveredAt() == null) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }
        
        shipment = orderShipmentRepository.save(shipment);
        
        // 如果物流狀態更新為「已出貨」，同時更新訂單狀態為 PROCESSING（處理中/已發貨）
        if (status == ShippingStatus.SHIPPED) {
            updateOrderStatusToProcessing(shipment.getOrderId());
        }
        
        // 記錄歷史
        orderHistoryService.recordHistory(shipment.getOrderId(), "UPDATE_SHIPPING_STATUS", 
            "更新物流狀態", oldStatus.name(), status.name(), null, null);
        
        return convertToDTO(shipment);
    }
    
    /**
     * 更新訂單狀態為處理中（當物流狀態為已出貨時）
     */
    private void updateOrderStatusToProcessing(Long orderId) {
        try {
            log.info("Attempting to update order status to PROCESSING for order: {}", orderId);
            
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
            
            log.info("Order found - ID: {}, Current status: {}", orderId, order.getStatus());
            
            // 如果訂單狀態是 PAID（已付款），更新為 PROCESSING（處理中/已發貨）
            if (order.getStatus() == OrderStatus.PAID) {
                OrderStatus oldStatus = order.getStatus();
                order.setStatus(OrderStatus.PROCESSING);
                orderRepository.save(order);
                orderRepository.flush(); // 確保狀態立即寫入數據庫
                
                log.info("Order status updated from {} to PROCESSING for order: {}", oldStatus, orderId);
                
                // 記錄歷史
                orderHistoryService.recordHistory(orderId, "ORDER_SHIPPED", 
                    "訂單已發貨", oldStatus.name(), OrderStatus.PROCESSING.name(), null, null);
            } else {
                log.warn("Order {} status is not PAID (current: {}), skipping status update to PROCESSING", 
                        orderId, order.getStatus());
            }
        } catch (Exception e) {
            // 記錄錯誤但不影響物流狀態更新
            log.error("Failed to update order status to PROCESSING for order: {}", orderId, e);
        }
    }

    /**
     * 更新物流單號
     */
    @Transactional
    public OrderShipmentDTO updateTrackingNumber(Long shipmentId, String trackingNumber) {
        OrderShipment shipment = orderShipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new RuntimeException("物流記錄不存在"));
        
        shipment.setTrackingNumber(trackingNumber);
        shipment = orderShipmentRepository.save(shipment);
        
        // 記錄歷史
        orderHistoryService.recordHistory(shipment.getOrderId(), "UPDATE_TRACKING_NUMBER", 
            "更新物流單號: " + trackingNumber, null, null, null, null);
        
        return convertToDTO(shipment);
    }

    /**
     * 取得訂單的物流記錄
     */
    @Transactional(readOnly = true)
    public List<OrderShipmentDTO> getShipmentsByOrderId(Long orderId) {
        return orderShipmentRepository.findByOrderId(orderId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 根據物流單號查詢
     */
    @Transactional(readOnly = true)
    public OrderShipmentDTO findByTrackingNumber(String trackingNumber) {
        OrderShipment shipment = orderShipmentRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new RuntimeException("找不到物流記錄"));
        return convertToDTO(shipment);
    }

    /**
     * 轉換 DTO 到 Entity
     */
    private OrderShipment convertToEntity(OrderShipmentDTO dto) {
        OrderShipment shipment = new OrderShipment();
        BeanUtils.copyProperties(dto, shipment);
        return shipment;
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private OrderShipmentDTO convertToDTO(OrderShipment shipment) {
        OrderShipmentDTO dto = new OrderShipmentDTO();
        BeanUtils.copyProperties(shipment, dto);
        return dto;
    }
}
