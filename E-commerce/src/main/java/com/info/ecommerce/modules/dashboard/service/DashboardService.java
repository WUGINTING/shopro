package com.info.ecommerce.modules.dashboard.service;

import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.dashboard.dto.DashboardStatsDTO;
import com.info.ecommerce.modules.dashboard.dto.RecentOrderDTO;
import com.info.ecommerce.modules.dashboard.dto.TopProductDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    /**
     * Get dashboard summary statistics
     */
    public DashboardStatsDTO getStats() {
        // Calculate current month stats
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);
        
        // Calculate previous month stats for comparison
        LocalDateTime startOfLastMonth = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = YearMonth.now().minusMonths(1).atEndOfMonth().atTime(23, 59, 59);

        // Total products count
        Long totalProducts = productRepository.count();
        Long totalProductsLastMonth = productRepository.countByCreatedAtBefore(startOfMonth);
        Double productChange = calculatePercentageChange(totalProductsLastMonth, totalProducts);

        // Pending orders count
        Long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        Long pendingOrdersLastMonth = orderRepository.countByStatusAndCreatedAtBefore(
            OrderStatus.PENDING, startOfMonth);
        Double pendingOrdersChange = calculatePercentageChange(pendingOrdersLastMonth, pendingOrders);

        // Total customers count
        Long totalCustomers = memberRepository.count();
        Long totalCustomersLastMonth = memberRepository.countByCreatedAtBefore(startOfMonth);
        Double customersChange = calculatePercentageChange(totalCustomersLastMonth, totalCustomers);

        // Monthly sales
        BigDecimal monthlySales = calculateMonthlySales(startOfMonth, endOfMonth);
        BigDecimal lastMonthSales = calculateMonthlySales(startOfLastMonth, endOfLastMonth);
        Double salesChange = calculatePercentageChange(
            lastMonthSales.doubleValue(), monthlySales.doubleValue());

        return DashboardStatsDTO.builder()
                .totalProducts(totalProducts)
                .totalProductsChange(productChange)
                .pendingOrders(pendingOrders)
                .pendingOrdersChange(pendingOrdersChange)
                .totalCustomers(totalCustomers)
                .totalCustomersChange(customersChange)
                .monthlySales(monthlySales)
                .monthlySalesChange(salesChange)
                .build();
    }

    /**
     * Get recent orders
     */
    public List<RecentOrderDTO> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Order> orders = orderRepository.findAll(pageable).getContent();

        return orders.stream()
                .map(order -> RecentOrderDTO.builder()
                        .id(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .status(order.getStatus().name())
                        .totalAmount(order.getTotalAmount())
                        .customerName(order.getCustomerName() != null ? 
                            order.getCustomerName() : "未知客戶")
                        .createdAt(order.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get top selling products
     * TODO: Implement proper sales calculation by joining with OrderItem entity
     * Currently returns recently added products as a placeholder
     */
    public List<TopProductDTO> getTopProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        return productRepository.findAll(pageable).getContent().stream()
                .map(product -> TopProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .salesCount(0L) // TODO: Calculate actual sales from order_items table
                        .price(product.getPrice())
                        .imageUrl(product.getImages() != null && !product.getImages().isEmpty() ? 
                            product.getImages().get(0) : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Calculate monthly sales
     */
    private BigDecimal calculateMonthlySales(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(start, end);
        return orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate percentage change
     */
    private Double calculatePercentageChange(Number oldValue, Number newValue) {
        if (oldValue == null || oldValue.doubleValue() == 0) {
            return newValue.doubleValue() > 0 ? 100.0 : 0.0;
        }
        
        double change = ((newValue.doubleValue() - oldValue.doubleValue()) / oldValue.doubleValue()) * 100;
        return BigDecimal.valueOf(change).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
