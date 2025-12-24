package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.order.dto.OrderStatisticsDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 訂單統計服務 - 數據儀表板
 */
@Service
@RequiredArgsConstructor
public class OrderStatisticsService {

    private final OrderRepository orderRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 取得訂單統計資料
     */
    @Transactional(readOnly = true)
    public OrderStatisticsDTO getStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
        
        OrderStatisticsDTO statistics = new OrderStatisticsDTO();
        
        // 總訂單數
        statistics.setTotalOrders((long) orders.size());
        
        // 總金額
        BigDecimal totalAmount = orders.stream()
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setTotalAmount(totalAmount);
        
        // 平均訂單金額
        BigDecimal avgAmount = orders.isEmpty() ? BigDecimal.ZERO :
            totalAmount.divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP);
        statistics.setAverageOrderAmount(avgAmount);
        
        // 每日訂單趨勢
        Map<String, Long> dailyOrderTrend = orders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getCreatedAt().toLocalDate().format(DATE_FORMATTER),
                Collectors.counting()
            ));
        statistics.setDailyOrderTrend(dailyOrderTrend);
        
        // 每日金額趨勢
        Map<String, BigDecimal> dailyAmountTrend = orders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getCreatedAt().toLocalDate().format(DATE_FORMATTER),
                Collectors.mapping(Order::getTotalAmount,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));
        statistics.setDailyAmountTrend(dailyAmountTrend);
        
        // 狀態占比
        Map<String, Long> statusDistribution = orders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getStatus().getDescription(),
                Collectors.counting()
            ));
        statistics.setStatusDistribution(statusDistribution);
        
        // 金額分布（按區間）- 優化為單次迭代
        Map<String, Long> amountDistribution = new HashMap<>();
        amountDistribution.put("0-1000", 0L);
        amountDistribution.put("1000-5000", 0L);
        amountDistribution.put("5000-10000", 0L);
        amountDistribution.put("10000+", 0L);
        
        for (Order order : orders) {
            BigDecimal amount = order.getTotalAmount();
            if (amount.compareTo(new BigDecimal("1000")) < 0) {
                amountDistribution.merge("0-1000", 1L, Long::sum);
            } else if (amount.compareTo(new BigDecimal("5000")) < 0) {
                amountDistribution.merge("1000-5000", 1L, Long::sum);
            } else if (amount.compareTo(new BigDecimal("10000")) < 0) {
                amountDistribution.merge("5000-10000", 1L, Long::sum);
            } else {
                amountDistribution.merge("10000+", 1L, Long::sum);
            }
        }
        statistics.setAmountDistribution(amountDistribution);
        
        return statistics;
    }

    /**
     * 取得指定狀態的訂單統計
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatisticsByStatus(OrderStatus status) {
        Long count = orderRepository.countByStatus(status);
        BigDecimal totalAmount = orderRepository.sumTotalAmountByStatus(status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", status.getDescription());
        result.put("count", count);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        return result;
    }

    /**
     * 取得今日訂單統計
     */
    @Transactional(readOnly = true)
    public OrderStatisticsDTO getTodayStatistics() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return getStatistics(startOfDay, endOfDay);
    }

    /**
     * 取得本週訂單統計
     */
    @Transactional(readOnly = true)
    public OrderStatisticsDTO getWeekStatistics() {
        LocalDateTime startOfWeek = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime endOfWeek = LocalDateTime.now();
        return getStatistics(startOfWeek, endOfWeek);
    }

    /**
     * 取得本月訂單統計
     */
    @Transactional(readOnly = true)
    public OrderStatisticsDTO getMonthStatistics() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDateTime.now();
        return getStatistics(startOfMonth, endOfMonth);
    }
}
