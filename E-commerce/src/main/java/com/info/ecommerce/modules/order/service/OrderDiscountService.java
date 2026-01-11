package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.OrderDiscountDTO;
import com.info.ecommerce.modules.order.entity.OrderDiscount;
import com.info.ecommerce.modules.order.repository.OrderDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 訂單折扣服務 - 折扣管理
 */
@Service
@RequiredArgsConstructor
public class OrderDiscountService {

    private final OrderDiscountRepository orderDiscountRepository;
    private final OrderHistoryService orderHistoryService;

    /**
     * 新增訂單折扣
     */
    @Transactional
    public OrderDiscountDTO addDiscount(OrderDiscountDTO dto) {
        OrderDiscount discount = convertToEntity(dto);
        discount = orderDiscountRepository.save(discount);
        
        // 記錄歷史
        orderHistoryService.recordHistory(dto.getOrderId(), "ADD_DISCOUNT", 
            "新增折扣: " + dto.getDiscountType() + " - " + dto.getDiscountAmount(), 
            null, null, null, null);
        
        return convertToDTO(discount);
    }

    /**
     * 取得訂單的所有折扣
     */
    @Transactional(readOnly = true)
    public List<OrderDiscountDTO> getDiscountsByOrderId(Long orderId) {
        return orderDiscountRepository.findByOrderId(orderId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 根據折扣代碼查詢
     */
    @Transactional(readOnly = true)
    public List<OrderDiscountDTO> findByDiscountCode(String discountCode) {
        return orderDiscountRepository.findByDiscountCode(discountCode)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 取得所有折扣
     */
    @Transactional(readOnly = true)
    public List<OrderDiscountDTO> getAllDiscounts() {
        return orderDiscountRepository.findAllOrderByCreatedAtDesc()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 更新訂單折扣
     */
    @Transactional
    public OrderDiscountDTO updateDiscount(Long discountId, OrderDiscountDTO dto) {
        OrderDiscount discount = orderDiscountRepository.findById(discountId)
            .orElseThrow(() -> new RuntimeException("折扣記錄不存在"));
        
        String oldDiscountType = discount.getDiscountType();
        BigDecimal oldDiscountAmount = discount.getDiscountAmount();
        
        // 更新字段
        discount.setOrderId(dto.getOrderId());
        discount.setDiscountType(dto.getDiscountType());
        discount.setDiscountCode(dto.getDiscountCode());
        discount.setDiscountAmount(dto.getDiscountAmount());
        discount.setDiscountPercentage(dto.getDiscountPercentage());
        discount.setDescription(dto.getDescription());
        
        discount = orderDiscountRepository.save(discount);
        
        // 記錄歷史
        orderHistoryService.recordHistory(dto.getOrderId(), "UPDATE_DISCOUNT", 
            "更新折扣: " + oldDiscountType + " - " + oldDiscountAmount + " -> " + 
            dto.getDiscountType() + " - " + dto.getDiscountAmount(), 
            null, null, null, null);
        
        return convertToDTO(discount);
    }

    /**
     * 刪除訂單折扣
     */
    @Transactional
    public void deleteDiscount(Long discountId) {
        OrderDiscount discount = orderDiscountRepository.findById(discountId)
            .orElseThrow(() -> new RuntimeException("折扣記錄不存在"));
        
        orderHistoryService.recordHistory(discount.getOrderId(), "DELETE_DISCOUNT", 
            "刪除折扣: " + discount.getDiscountType(), null, null, null, null);
        
        orderDiscountRepository.delete(discount);
    }

    /**
     * 轉換 DTO 到 Entity
     */
    private OrderDiscount convertToEntity(OrderDiscountDTO dto) {
        OrderDiscount discount = new OrderDiscount();
        BeanUtils.copyProperties(dto, discount);
        return discount;
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private OrderDiscountDTO convertToDTO(OrderDiscount discount) {
        OrderDiscountDTO dto = new OrderDiscountDTO();
        BeanUtils.copyProperties(discount, dto);
        return dto;
    }
}
