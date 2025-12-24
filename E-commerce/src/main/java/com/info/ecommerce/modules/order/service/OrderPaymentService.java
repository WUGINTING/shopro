package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.OrderPaymentDTO;
import com.info.ecommerce.modules.order.entity.OrderPayment;
import com.info.ecommerce.modules.order.enums.PaymentStatus;
import com.info.ecommerce.modules.order.repository.OrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 付款服務 - 金流管理
 */
@Service
@RequiredArgsConstructor
public class OrderPaymentService {

    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderHistoryService orderHistoryService;

    /**
     * 創建付款記錄
     */
    @Transactional
    public OrderPaymentDTO createPayment(OrderPaymentDTO dto) {
        OrderPayment payment = convertToEntity(dto);
        payment = orderPaymentRepository.save(payment);
        
        // 記錄歷史
        orderHistoryService.recordHistory(dto.getOrderId(), "CREATE_PAYMENT", 
            "建立付款記錄: " + dto.getPaymentMethod(), 
            null, dto.getPaymentStatus().name(), null, null);
        
        return convertToDTO(payment);
    }

    /**
     * 更新付款狀態
     */
    @Transactional
    public OrderPaymentDTO updatePaymentStatus(Long paymentId, PaymentStatus status) {
        OrderPayment payment = orderPaymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("付款記錄不存在"));
        
        PaymentStatus oldStatus = payment.getPaymentStatus();
        payment.setPaymentStatus(status);
        
        if (status == PaymentStatus.PAID && payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        } else if (status == PaymentStatus.REFUNDED && payment.getRefundTime() == null) {
            payment.setRefundTime(LocalDateTime.now());
        }
        
        payment = orderPaymentRepository.save(payment);
        
        // 記錄歷史
        orderHistoryService.recordHistory(payment.getOrderId(), "UPDATE_PAYMENT_STATUS", 
            "更新付款狀態", oldStatus.name(), status.name(), null, null);
        
        return convertToDTO(payment);
    }

    /**
     * 取得訂單的付款記錄
     */
    @Transactional(readOnly = true)
    public List<OrderPaymentDTO> getPaymentsByOrderId(Long orderId) {
        return orderPaymentRepository.findByOrderId(orderId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 申請退款
     */
    @Transactional
    public OrderPaymentDTO requestRefund(Long paymentId, java.math.BigDecimal refundAmount) {
        OrderPayment payment = orderPaymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("付款記錄不存在"));
        
        payment.setPaymentStatus(PaymentStatus.REFUNDING);
        payment.setRefundAmount(refundAmount);
        payment = orderPaymentRepository.save(payment);
        
        // 記錄歷史
        orderHistoryService.recordHistory(payment.getOrderId(), "REQUEST_REFUND", 
            "申請退款: " + refundAmount, 
            PaymentStatus.PAID.name(), PaymentStatus.REFUNDING.name(), null, null);
        
        return convertToDTO(payment);
    }

    /**
     * 轉換 DTO 到 Entity
     */
    private OrderPayment convertToEntity(OrderPaymentDTO dto) {
        OrderPayment payment = new OrderPayment();
        BeanUtils.copyProperties(dto, payment);
        return payment;
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private OrderPaymentDTO convertToDTO(OrderPayment payment) {
        OrderPaymentDTO dto = new OrderPaymentDTO();
        BeanUtils.copyProperties(payment, dto);
        return dto;
    }
}
