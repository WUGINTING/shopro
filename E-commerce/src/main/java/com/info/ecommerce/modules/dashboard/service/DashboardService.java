package com.info.ecommerce.modules.dashboard.service;

import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.dashboard.dto.DashboardLookupsDTO;
import com.info.ecommerce.modules.dashboard.dto.DashboardOptionDTO;
import com.info.ecommerce.modules.dashboard.dto.DashboardOverviewDTO;
import com.info.ecommerce.modules.dashboard.dto.DashboardShortcutDTO;
import com.info.ecommerce.modules.dashboard.dto.DashboardStatsDTO;
import com.info.ecommerce.modules.dashboard.dto.RecentOrderDTO;
import com.info.ecommerce.modules.dashboard.dto.TopProductDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.auth.entity.Role;
import com.info.ecommerce.modules.system.enums.PaymentMethod;
import com.info.ecommerce.modules.system.enums.ShippingMethod;
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
import java.util.Arrays;
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
        Long pendingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        Long pendingOrdersLastMonth = orderRepository.countByStatusAndCreatedAtBefore(
            OrderStatus.PROCESSING, startOfMonth);
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
     * Unified dashboard payload to reduce admin page round-trips.
     */
    public DashboardOverviewDTO getOverview(int recentOrderLimit, int topProductLimit) {
        DashboardStatsDTO stats = getStats();
        List<RecentOrderDTO> recentOrders = getRecentOrders(recentOrderLimit);
        List<TopProductDTO> topProducts = getTopProducts(topProductLimit);

        return DashboardOverviewDTO.builder()
                .stats(stats)
                .recentOrders(recentOrders)
                .topProducts(topProducts)
                .lookups(buildLookups())
                .shortcuts(buildShortcuts(stats))
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
                            order.getCustomerName() : "\u672A\u77E5\u5BA2\u6236")
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
                        .price(product.getBasePrice())
                        .imageUrl(product.getImageUrls() != null && !product.getImageUrls().isEmpty() ?
                            product.getImageUrls().get(0) : null)
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

    private DashboardLookupsDTO buildLookups() {
        return DashboardLookupsDTO.builder()
                .roles(Arrays.stream(Role.values())
                        .map(role -> DashboardOptionDTO.builder()
                                .code(role.name())
                                .label(formatRole(role))
                                .build())
                        .toList())
                .orderStatuses(Arrays.stream(OrderStatus.values())
                        .map(status -> DashboardOptionDTO.builder()
                                .code(status.name())
                                .label(formatOrderStatus(status))
                                .build())
                        .toList())
                .paymentMethods(Arrays.stream(PaymentMethod.values())
                        .map(method -> DashboardOptionDTO.builder()
                                .code(method.name())
                                .label(formatPaymentMethod(method))
                                .build())
                        .toList())
                .shippingMethods(Arrays.stream(ShippingMethod.values())
                        .map(method -> DashboardOptionDTO.builder()
                                .code(method.name())
                                .label(formatShippingMethod(method))
                                .build())
                        .toList())
                .pickupTypes(Arrays.stream(PickupType.values())
                        .map(type -> DashboardOptionDTO.builder()
                                .code(type.name())
                                .label(formatPickupType(type))
                                .build())
                        .toList())
                .build();
    }

    private List<DashboardShortcutDTO> buildShortcuts(DashboardStatsDTO stats) {
        Long pendingPaymentCount = orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT);
        Long processingCount = orderRepository.countByStatus(OrderStatus.PROCESSING);

        return List.of(
                DashboardShortcutDTO.builder()
                        .key("orders")
                        .label("Orders")
                        .route("/admin/orders")
                        .description("Manage order lifecycle and status updates")
                        .badgeCount(processingCount)
                        .build(),
                DashboardShortcutDTO.builder()
                        .key("products")
                        .label("Products")
                        .route("/admin/products")
                        .description("Edit catalog, pricing and publication status")
                        .badgeCount(stats.getTotalProducts())
                        .build(),
                DashboardShortcutDTO.builder()
                        .key("customers")
                        .label("Customers")
                        .route("/admin/customers")
                        .description("Customer records and service operations")
                        .badgeCount(stats.getTotalCustomers())
                        .build(),
                DashboardShortcutDTO.builder()
                        .key("payments")
                        .label("Payments")
                        .route("/admin/payment-transactions")
                        .description("Track payment transactions and callback logs")
                        .badgeCount(pendingPaymentCount)
                        .build()
        );
    }

    private String formatRole(Role role) {
        return switch (role) {
            case ADMIN -> "Admin";
            case MANAGER -> "Manager";
            case STAFF -> "Staff";
            case CUSTOMER -> "Customer";
        };
    }

    private String formatOrderStatus(OrderStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> "Pending Payment";
            case PAID -> "Paid";
            case PROCESSING -> "Processing";
            case COMPLETED -> "Completed";
            case CANCELLED -> "Cancelled";
            case REFUNDED -> "Refunded";
        };
    }

    private String formatPaymentMethod(PaymentMethod method) {
        return switch (method) {
            case CREDIT_CARD -> "Credit Card";
            case ATM -> "ATM Transfer";
            case CVS -> "Convenience Store";
            case LINE_PAY -> "LINE Pay";
            case APPLE_PAY -> "Apple Pay";
            case GOOGLE_PAY -> "Google Pay";
            case COD -> "Cash on Delivery";
            case STORE_PAYMENT -> "Store Payment";
        };
    }

    private String formatShippingMethod(ShippingMethod method) {
        return switch (method) {
            case HOME_DELIVERY -> "Home Delivery";
            case STORE_PICKUP -> "Store Pickup";
            case CVS_PICKUP -> "Convenience Store Pickup";
            case SELF_PICKUP -> "Self Pickup";
        };
    }

    private String formatPickupType(PickupType type) {
        return switch (type) {
            case DELIVERY -> "Delivery";
            case STORE_PICKUP -> "Store Pickup";
            case CROSS_STORE_PICKUP -> "Cross-store Pickup";
        };
    }
}
