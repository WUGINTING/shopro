package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.OrderShipmentDTO;
import com.info.ecommerce.modules.order.entity.OrderShipment;
import com.info.ecommerce.modules.order.enums.ShippingStatus;
import com.info.ecommerce.modules.order.repository.OrderShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物流服務 - 物流管理
 */
@Service
@RequiredArgsConstructor
public class OrderShipmentService {

    private final OrderShipmentRepository orderShipmentRepository;
    private final OrderHistoryService orderHistoryService;

    /**
     * 創建物流記錄
     */
    @Transactional
    public OrderShipmentDTO createShipment(OrderShipmentDTO dto) {
        OrderShipment shipment = convertToEntity(dto);
        shipment = orderShipmentRepository.save(shipment);
        
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
        
        // 記錄歷史
        orderHistoryService.recordHistory(shipment.getOrderId(), "UPDATE_SHIPPING_STATUS", 
            "更新物流狀態", oldStatus.name(), status.name(), null, null);
        
        return convertToDTO(shipment);
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
