package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.OrderDiscountDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderDiscount;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderDiscountRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 訂單折扣服務 - 折扣管理
 * 折扣會實際反映在訂單金額：訂單折扣金額 = 該訂單所有折扣紀錄的總和（不超過商品小計），
 * 總金額 = 小計 - 折扣 + 運費。只有待付款的訂單可以調整折扣，已付款訂單請走退款。
 */
@Service
@RequiredArgsConstructor
public class OrderDiscountService {

    private final OrderDiscountRepository orderDiscountRepository;
    private final OrderHistoryService orderHistoryService;
    private final OrderRepository orderRepository;

    /**
     * 新增訂單折扣
     */
    @Transactional
    public OrderDiscountDTO addDiscount(OrderDiscountDTO dto) {
        Order order = lockAdjustableOrder(dto.getOrderId());
        OrderDiscount discount = convertToEntity(dto);
        discount.setId(null);
        discount.setDiscountAmount(resolveAmount(order, dto));
        discount = orderDiscountRepository.save(discount);
        applyDiscountsToOrder(order);
        
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
            .orElseThrow(() -> new BusinessException("折扣記錄不存在"));

        String oldDiscountType = discount.getDiscountType();
        BigDecimal oldDiscountAmount = discount.getDiscountAmount();
        Order order = lockAdjustableOrder(discount.getOrderId());
        dto.setOrderId(discount.getOrderId()); // 折扣不可搬到其他訂單

        // 更新字段
        discount.setDiscountType(dto.getDiscountType());
        discount.setDiscountCode(dto.getDiscountCode());
        discount.setDiscountAmount(resolveAmount(order, dto));
        discount.setDiscountPercentage(dto.getDiscountPercentage());
        discount.setDescription(dto.getDescription());
        
        discount = orderDiscountRepository.save(discount);
        applyDiscountsToOrder(order);

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
            .orElseThrow(() -> new BusinessException("折扣記錄不存在"));
        Order order = lockAdjustableOrder(discount.getOrderId());

        orderHistoryService.recordHistory(discount.getOrderId(), "DELETE_DISCOUNT",
            "刪除折扣: " + discount.getDiscountType(), null, null, null, null);

        orderDiscountRepository.delete(discount);
        orderDiscountRepository.flush();
        applyDiscountsToOrder(order);
    }

    /** 鎖定訂單並確認仍可調整金額（只限待付款） */
    private Order lockAdjustableOrder(Long orderId) {
        if (orderId == null) {
            throw new BusinessException("訂單 ID 不能為空");
        }
        Order order = orderRepository.findByIdForUpdate(orderId)
            .orElseThrow(() -> new BusinessException("訂單不存在"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("只有待付款的訂單可以調整折扣；已付款訂單請改用退款處理");
        }
        return order;
    }

    /** 折扣金額：有填金額用金額；只填百分比時依商品小計計算 */
    private BigDecimal resolveAmount(Order order, OrderDiscountDTO dto) {
        BigDecimal amount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
        if (amount.signum() < 0) {
            throw new BusinessException("折扣金額不能小於 0");
        }
        BigDecimal percentage = dto.getDiscountPercentage();
        if (amount.signum() == 0 && percentage != null && percentage.signum() > 0) {
            if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BusinessException("折扣百分比不可超過 100");
            }
            BigDecimal subtotal = order.getSubtotalAmount() != null ? order.getSubtotalAmount() : BigDecimal.ZERO;
            amount = subtotal.multiply(percentage).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        }
        return amount;
    }

    /** 以所有折扣紀錄重新計算訂單折扣與總金額 */
    private void applyDiscountsToOrder(Order order) {
        BigDecimal subtotal = order.getSubtotalAmount() != null ? order.getSubtotalAmount() : BigDecimal.ZERO;
        BigDecimal shipping = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discount = orderDiscountRepository.findByOrderId(order.getId()).stream()
            .map(OrderDiscount::getDiscountAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .min(subtotal);
        order.setDiscountAmount(discount);
        order.setTotalAmount(subtotal.subtract(discount).add(shipping));
        orderRepository.save(order);
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
