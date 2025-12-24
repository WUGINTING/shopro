package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.BatchOrderUpdateDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 訂單批次操作服務
 */
@Service
@RequiredArgsConstructor
public class OrderBatchService {

    private final OrderRepository orderRepository;
    private final OrderHistoryService orderHistoryService;

    /**
     * 批次更新訂單狀態
     */
    @Transactional
    public List<Long> batchUpdateStatus(BatchOrderUpdateDTO dto) {
        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        
        for (Long orderId : dto.getOrderIds()) {
            try {
                Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new BusinessException("訂單不存在: " + orderId));
                
                OrderStatus oldStatus = order.getStatus();
                order.setStatus(dto.getTargetStatus());
                
                if (dto.getTargetStatus() == OrderStatus.COMPLETED && order.getCompletedAt() == null) {
                    order.setCompletedAt(LocalDateTime.now());
                }
                
                orderRepository.save(order);
                
                // 記錄歷史
                orderHistoryService.recordHistory(orderId, "BATCH_UPDATE_STATUS", 
                    "批次更新訂單狀態: " + (dto.getNotes() != null ? dto.getNotes() : ""), 
                    oldStatus.name(), dto.getTargetStatus().name(), 
                    dto.getOperatorId(), dto.getOperatorName());
                
                successIds.add(orderId);
            } catch (Exception e) {
                failedIds.add(orderId);
            }
        }
        
        if (!failedIds.isEmpty()) {
            throw new BusinessException("部分訂單更新失敗，失敗的訂單 ID: " + failedIds);
        }
        
        return successIds;
    }

    /**
     * 批次刪除訂單
     */
    @Transactional
    public void batchDeleteOrders(List<Long> orderIds) {
        for (Long orderId : orderIds) {
            orderRepository.findById(orderId).ifPresent(order -> {
                orderHistoryService.recordHistory(orderId, "BATCH_DELETE", "批次刪除訂單", 
                    order.getStatus().name(), null, null, null);
                orderRepository.delete(order);
            });
        }
    }

    /**
     * 導出訂單資料（返回訂單列表供導出使用）
     */
    @Transactional(readOnly = true)
    public List<Order> exportOrders(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return orderRepository.findAll();
        }
        return orderRepository.findAllById(orderIds);
    }
}
