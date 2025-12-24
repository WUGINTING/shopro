package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.OrderHistoryDTO;
import com.info.ecommerce.modules.order.entity.OrderHistory;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 訂單歷史服務 - 歷史紀錄管理
 */
@Service
@RequiredArgsConstructor
public class OrderHistoryService {

    private final OrderHistoryRepository orderHistoryRepository;

    /**
     * 記錄訂單操作歷史
     */
    @Transactional
    public void recordHistory(Long orderId, String actionType, String actionDescription, 
                             String oldStatus, String newStatus, Long operatorId, String operatorName) {
        OrderHistory history = OrderHistory.builder()
            .orderId(orderId)
            .actionType(actionType)
            .actionDescription(actionDescription)
            .oldStatus(oldStatus)
            .newStatus(newStatus)
            .operatorId(operatorId)
            .operatorName(operatorName)
            .build();
        
        orderHistoryRepository.save(history);
    }

    /**
     * 取得訂單歷史記錄
     */
    @Transactional(readOnly = true)
    public List<OrderHistoryDTO> getOrderHistory(Long orderId) {
        return orderHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 分頁取得訂單歷史記錄
     */
    @Transactional(readOnly = true)
    public Page<OrderHistoryDTO> getOrderHistoryPage(Long orderId, Pageable pageable) {
        return orderHistoryRepository.findByOrderId(orderId, pageable)
            .map(this::convertToDTO);
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private OrderHistoryDTO convertToDTO(OrderHistory history) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        BeanUtils.copyProperties(history, dto);
        return dto;
    }
}
