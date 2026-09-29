package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.OrderShipmentDTO;
import com.info.ecommerce.modules.order.event.OrderEmailEvent;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import org.springframework.context.ApplicationEventPublisher;
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
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 創建物流記錄
     * 已付款 / 處理中的訂單可出貨；待付款訂單只有貨到付款或後台建立（非線上付款）的訂單可以出貨
     */
    @Transactional
    public OrderShipmentDTO createShipment(OrderShipmentDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new BusinessException("訂單不存在"));
        assertShippable(order);
        if (dto.getShippingStatus() == null) {
            dto.setShippingStatus(ShippingStatus.PENDING);
        }

        OrderShipment shipment = convertToEntity(dto);
        shipment.setId(null);

        // 如果創建時狀態就是 SHIPPED，設置出貨時間
        if (dto.getShippingStatus() == ShippingStatus.SHIPPED && shipment.getShippedAt() == null) {
            shipment.setShippedAt(LocalDateTime.now());
        }

        shipment = orderShipmentRepository.save(shipment);

        // 記錄歷史
        orderHistoryService.recordHistory(dto.getOrderId(), "CREATE_SHIPMENT",
            "建立物流記錄: " + dto.getShippingCompany(),
            null, dto.getShippingStatus().name(), null, null);

        if (dto.getShippingStatus() == ShippingStatus.SHIPPED) {
            onShipped(shipment);
        } else if (dto.getShippingStatus() == ShippingStatus.DELIVERED) {
            onDelivered(shipment);
        }

        return convertToDTO(shipment);
    }

    /**
     * 更新物流狀態
     */
    @Transactional
    public OrderShipmentDTO updateShippingStatus(Long shipmentId, ShippingStatus status) {
        OrderShipment shipment = orderShipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new BusinessException("物流記錄不存在"));

        ShippingStatus oldStatus = shipment.getShippingStatus();
        if (oldStatus == status) {
            return convertToDTO(shipment);
        }
        if (status == ShippingStatus.SHIPPED || status == ShippingStatus.DELIVERED) {
            OrderStatus orderStatus = orderRepository.findById(shipment.getOrderId())
                .map(Order::getStatus).orElse(null);
            if (orderStatus == OrderStatus.CANCELLED || orderStatus == OrderStatus.REFUNDED) {
                throw new BusinessException("訂單已取消或已退款，無法更新為出貨 / 送達");
            }
        }
        shipment.setShippingStatus(status);

        if ((status == ShippingStatus.SHIPPED || status == ShippingStatus.DELIVERED) && shipment.getShippedAt() == null) {
            shipment.setShippedAt(LocalDateTime.now());
        }
        if (status == ShippingStatus.DELIVERED && shipment.getDeliveredAt() == null) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }

        shipment = orderShipmentRepository.save(shipment);

        // 記錄歷史
        orderHistoryService.recordHistory(shipment.getOrderId(), "UPDATE_SHIPPING_STATUS",
            "更新物流狀態", oldStatus.name(), status.name(), null, null);

        if (status == ShippingStatus.SHIPPED) {
            onShipped(shipment);
        } else if (status == ShippingStatus.DELIVERED) {
            onDelivered(shipment);
        }

        return convertToDTO(shipment);
    }

    private void assertShippable(Order order) {
        OrderStatus status = order.getStatus();
        if (status == OrderStatus.PAID || status == OrderStatus.PROCESSING) {
            return;
        }
        if (status == OrderStatus.PENDING_PAYMENT) {
            boolean onlinePayment = orderHistoryRepository
                .findByOrderIdAndActionType(order.getId(), StorefrontCheckoutService.ACTION_STOREFRONT_CHECKOUT).stream()
                .anyMatch(history -> StorefrontCheckoutService.PAYMENT_ECPAY.equals(history.getNewStatus()));
            if (onlinePayment) {
                throw new BusinessException("此訂單選擇線上付款且尚未付款，付款完成後才能出貨");
            }
            return;
        }
        throw new BusinessException("訂單狀態為「" + status.getDescription() + "」，無法出貨");
    }

    /** 已出貨：訂單改為處理中，並寄送出貨通知（含物流單號） */
    private void onShipped(OrderShipment shipment) {
        Order order = orderRepository.findById(shipment.getOrderId()).orElse(null);
        if (order == null) {
            return;
        }
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            orderService.updateOrderStatus(order.getId(), OrderStatus.PROCESSING, null, "系統");
            orderHistoryService.recordHistory(order.getId(), "ORDER_SHIPPED", "訂單已出貨", null, null, null, null);
        }
        eventPublisher.publishEvent(new OrderEmailEvent(order.getId(), OrderEmailEvent.Type.SHIPPED));
    }

    /** 已送達：所有物流都送達時，訂單自動完成 */
    private void onDelivered(OrderShipment shipment) {
        Order order = orderRepository.findById(shipment.getOrderId()).orElse(null);
        if (order == null || (order.getStatus() != OrderStatus.PROCESSING && order.getStatus() != OrderStatus.PAID
                && order.getStatus() != OrderStatus.PENDING_PAYMENT)) {
            return;
        }
        boolean allDelivered = orderShipmentRepository.findByOrderId(order.getId()).stream()
            .allMatch(item -> item.getShippingStatus() == ShippingStatus.DELIVERED);
        if (allDelivered) {
            orderService.updateOrderStatus(order.getId(), OrderStatus.COMPLETED, null, "系統");
        }
    }

    /**
     * 更新物流單號
     */
    @Transactional
    public OrderShipmentDTO updateTrackingNumber(Long shipmentId, String trackingNumber) {
        OrderShipment shipment = orderShipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new BusinessException("物流記錄不存在"));
        
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
            .orElseThrow(() -> new BusinessException("找不到物流記錄"));
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
