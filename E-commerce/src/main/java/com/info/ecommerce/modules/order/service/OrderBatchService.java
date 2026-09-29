package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.event.OrderEmailEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.dto.BatchOrderUpdateDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.crm.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OrderBatchService {

    private final OrderRepository orderRepository;
    private final OrderHistoryService orderHistoryService;
    private final MemberService memberService;
    private final OrderStockService orderStockService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderService orderService;
    private final com.info.ecommerce.modules.order.repository.OrderItemRepository orderItemRepository;

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
                // 不允許的狀態變更（例如已付款直接取消）跳過此筆，在交易外判斷避免整批回滾
                if (!OrderStatusRules.canChange(oldStatus, dto.getTargetStatus())
                        || (dto.getTargetStatus() == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED
                            && orderService.hasBeenPaid(orderId))) {
                    log.info("Batch status update skipped for order {}: {} -> {} not allowed", orderId, oldStatus, dto.getTargetStatus());
                    failedIds.add(orderId);
                    continue;
                }
                order.setStatus(dto.getTargetStatus());
                
                if (dto.getTargetStatus() == OrderStatus.COMPLETED && order.getCompletedAt() == null) {
                    order.setCompletedAt(LocalDateTime.now());
                }
                
                orderRepository.save(order);

                if (oldStatus == OrderStatus.CANCELLED && dto.getTargetStatus() != OrderStatus.CANCELLED) {
                    orderStockService.reserveAgain(orderId, order.getOrderNumber());
                }
                if (dto.getTargetStatus() == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
                    orderStockService.release(orderId, order.getOrderNumber());
                    eventPublisher.publishEvent(new OrderEmailEvent(orderId, OrderEmailEvent.Type.CANCELLED));
                } else if (dto.getTargetStatus() == OrderStatus.PAID && oldStatus != OrderStatus.PAID) {
                    eventPublisher.publishEvent(new OrderEmailEvent(orderId, OrderEmailEvent.Type.PAID));
                }
                
                // 記錄歷史
                orderHistoryService.recordHistory(orderId, "BATCH_UPDATE_STATUS", 
                    "批次更新訂單狀態: " + (dto.getNotes() != null ? dto.getNotes() : ""), 
                    oldStatus.name(), dto.getTargetStatus().name(), 
                    dto.getOperatorId(), dto.getOperatorName());
                
                // 依訂單重新計算會員累計消費（取消、退款會一併扣除）
                if (oldStatus != dto.getTargetStatus()) {
                    try {
                        memberService.syncTotalSpent(order.getCustomerId());
                    } catch (Exception e) {
                        log.warn("Failed to update member total spent in batch update for order {}", orderId, e);
                    }
                }
                
                successIds.add(orderId);
            } catch (Exception e) {
                failedIds.add(orderId);
            }
        }
        
        if (!failedIds.isEmpty()) {
            throw new BusinessException("部分訂單更新失敗（整批未套用，請排除後重試），失敗的訂單 ID: " + failedIds);
        }
        
        return successIds;
    }

    /**
     * 批次刪除訂單
     */
    @Transactional
    public void batchDeleteOrders(List<Long> orderIds) {
        // 與單筆刪除相同流程：歸還庫存與優惠券、刪除訂單品項並記錄歷程
        for (Long orderId : orderIds) {
            if (orderRepository.findById(orderId).isPresent()) {
                orderService.deleteOrder(orderId);
            }
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

    /**
     * 匯出訂單 CSV（最多 1 年）：每筆訂單一列，商品明細以「名稱 x 數量」串接
     */
    @Transactional(readOnly = true)
    public byte[] exportCsv(java.time.LocalDate startDate, java.time.LocalDate endDate, OrderStatus status) {
        java.time.LocalDate end = endDate != null ? endDate : java.time.LocalDate.now();
        java.time.LocalDate start = startDate != null ? startDate : end.minusDays(30);
        if (start.isAfter(end) || start.plusDays(366).isBefore(end)) {
            throw new BusinessException("匯出期間需在一年內，且開始日期不可晚於結束日期");
        }
        List<Order> orders = orderRepository.findByCreatedAtBetween(start.atStartOfDay(), end.plusDays(1).atStartOfDay().minusNanos(1))
                .stream()
                .filter(order -> status == null || order.getStatus() == status)
                .sorted(java.util.Comparator.comparing(Order::getCreatedAt))
                .toList();
        java.util.Map<Long, List<com.info.ecommerce.modules.order.entity.OrderItem>> itemsByOrder = orders.isEmpty()
                ? java.util.Map.of()
                : orderItemRepository.findByOrderIdInBatches(orders.stream().map(Order::getId).toList()).stream()
                        .collect(java.util.stream.Collectors.groupingBy(com.info.ecommerce.modules.order.entity.OrderItem::getOrderId));

        StringBuilder sb = new StringBuilder("\uFEFF");
        sb.append("訂單編號,建立時間,狀態,顧客,Email,電話,配送方式,收件地址,商品明細,小計,折扣,運費,總額,備註\r\n");
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Order order : orders) {
            String items = itemsByOrder.getOrDefault(order.getId(), List.of()).stream()
                    .map(item -> (item.getProductName() != null ? item.getProductName() : "#" + item.getProductId())
                            + (item.getProductSpec() != null ? "(" + item.getProductSpec() + ")" : "")
                            + " x " + item.getQuantity())
                    .collect(java.util.stream.Collectors.joining("；"));
            sb.append(String.join(",",
                    csv(order.getOrderNumber()),
                    csv(order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : ""),
                    csv(order.getStatus() != null ? order.getStatus().getDescription() : ""),
                    csv(order.getCustomerName()),
                    csv(order.getCustomerEmail()),
                    csv(order.getCustomerPhone()),
                    csv(order.getPickupType() == com.info.ecommerce.modules.order.enums.PickupType.STORE_PICKUP ? "門市自取" : "宅配"),
                    csv(order.getShippingAddress()),
                    csv(items),
                    csv(plain(order.getSubtotalAmount())),
                    csv(plain(order.getDiscountAmount())),
                    csv(plain(order.getShippingFee())),
                    csv(plain(order.getTotalAmount())),
                    csv(order.getNotes()))).append("\r\n");
        }
        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static String plain(java.math.BigDecimal value) {
        return value == null ? "0" : value.stripTrailingZeros().toPlainString();
    }

    /** CSV 欄位跳脫；開頭為公式字元時加上單引號，避免 Excel 公式注入 */
    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        String text = value.replace("\r", " ").replace("\n", " ");
        if (!text.isEmpty() && "=+-@\t\r".indexOf(text.charAt(0)) >= 0) {
            text = "'" + text;
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
