package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.OrderRefundRequest;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderPayment;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PaymentStatus;
import com.info.ecommerce.modules.order.repository.OrderPaymentRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 訂單退款登記
 *
 * 系統不會自動呼叫綠界退款：信用卡請至綠界廠商後台「信用卡收單 > 交易明細」執行退刷，
 * ATM / 超商代碼付款則需以匯款方式退還。完成後在此登記，系統會：
 * - 記錄退款金額與原因到付款紀錄與訂單歷程
 * - 全額退款時將訂單改為「已退款」、扣回會員累計消費、寄送退款通知
 * - 勾選歸還庫存時，將商品數量加回庫存（商品已退回或尚未出貨時）
 */
@Service
@RequiredArgsConstructor
public class OrderRefundService {

    public static final String ACTION_REFUND = "REFUND";

    private final OrderRepository orderRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderService orderService;
    private final OrderStockService orderStockService;
    private final OrderHistoryService orderHistoryService;

    @Transactional
    public OrderDTO refund(Long orderId, OrderRefundRequest request, Long operatorId, String operatorName) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new BusinessException("訂單不存在"));
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.PROCESSING
                && order.getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessException("訂單狀態為「" + order.getStatus().getDescription() + "」，無法退款；未付款的訂單請直接取消");
        }

        // 貨到付款尚未收款（處理中）的訂單沒有錢可退；已完成的貨到付款訂單視為已收款
        if (order.getStatus() != OrderStatus.COMPLETED && !orderService.hasBeenPaid(orderId)) {
            throw new BusinessException("此訂單尚未收款，無法退款；如需取消請使用「取消訂單」");
        }

        List<OrderPayment> payments = orderPaymentRepository.findByOrderId(orderId);
        BigDecimal alreadyRefunded = payments.stream()
                .map(OrderPayment::getRefundAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal refundable = total.subtract(alreadyRefunded).max(BigDecimal.ZERO);
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : refundable;
        if (amount.signum() <= 0 || amount.compareTo(refundable) > 0) {
            throw new BusinessException("退款金額需介於 1 到可退金額 NT$" + refundable.stripTrailingZeros().toPlainString() + " 之間");
        }
        boolean fullRefund = amount.compareTo(refundable) == 0;
        if (request.isRestock() && !fullRefund) {
            throw new BusinessException("部分退款無法自動歸還整筆訂單的庫存；如有退回商品，請到商品頁手動補貨");
        }

        // 記錄到付款紀錄（沒有付款紀錄時，例如貨到付款，建立一筆退款紀錄）
        OrderPayment payment = payments.stream()
                .filter(item -> item.getPaymentStatus() == PaymentStatus.PAID || item.getPaymentStatus() == PaymentStatus.REFUNDING
                        || item.getPaymentStatus() == PaymentStatus.REFUNDED)
                .reduce((first, second) -> second)
                .orElseGet(() -> OrderPayment.builder()
                        .orderId(orderId)
                        .paymentStatus(PaymentStatus.PAID)
                        .paymentMethod("OTHER")
                        .paymentAmount(total)
                        .build());
        payment.setRefundAmount((payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO).add(amount));
        payment.setRefundTime(LocalDateTime.now());
        if (fullRefund) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
        }
        String note = "退款 NT$" + amount.stripTrailingZeros().toPlainString() + "：" + request.getReason().trim();
        payment.setNotes(payment.getNotes() == null || payment.getNotes().isBlank() ? note : payment.getNotes() + "\n" + note);
        orderPaymentRepository.save(payment);

        orderHistoryService.recordHistory(orderId, ACTION_REFUND,
                (fullRefund ? "全額退款" : "部分退款") + " NT$" + amount.stripTrailingZeros().toPlainString()
                        + "：" + request.getReason().trim() + (request.isRestock() ? "（歸還庫存）" : ""),
                null, amount.toPlainString(), operatorId, operatorName);

        if (request.isRestock()) {
            // 退款不歸還優惠券：優惠券已實際使用
            orderStockService.release(orderId, order.getOrderNumber(), false);
        }
        if (fullRefund) {
            return orderService.updateOrderStatus(orderId, OrderStatus.REFUNDED, operatorId, operatorName);
        }
        return orderService.getOrder(orderId);
    }
}
