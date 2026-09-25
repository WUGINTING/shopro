package com.info.ecommerce.modules.statistics.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.crm.service.MemberService;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.entity.OrderHistory;
import com.info.ecommerce.modules.order.entity.OrderItem;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import com.info.ecommerce.modules.order.repository.OrderItemRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.order.service.StorefrontCheckoutService;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductCategory;
import com.info.ecommerce.modules.product.repository.ProductCategoryRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.statistics.dto.StatisticsOverviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 營運統計：依訂單、訂單品項即時計算（資料量大時可改為每日彙總表）
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private static final int MAX_RANGE_DAYS = 366;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public StatisticsOverviewDTO overview(LocalDate startDate, LocalDate endDate) {
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDate start = startDate != null ? startDate : end.minusDays(29);
        if (start.isAfter(end)) {
            throw new BusinessException("開始日期不可晚於結束日期");
        }
        if (start.plusDays(MAX_RANGE_DAYS).isBefore(end)) {
            throw new BusinessException("統計期間最長一年");
        }
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.plusDays(1).atStartOfDay().minusNanos(1);

        List<Order> orders = orderRepository.findByCreatedAtBetween(from, to);
        List<Order> sold = orders.stream()
                .filter(order -> MemberService.SPENDING_STATUSES.contains(order.getStatus()))
                .toList();

        BigDecimal totalSales = sum(sold, Order::getTotalAmount);
        long customers = sold.stream()
                .map(order -> order.getCustomerId() != null ? "id:" + order.getCustomerId()
                        : "email:" + String.valueOf(order.getCustomerEmail()).toLowerCase(Locale.ROOT))
                .distinct().count();
        BigDecimal average = sold.isEmpty() ? BigDecimal.ZERO
                : totalSales.divide(BigDecimal.valueOf(sold.size()), 0, RoundingMode.HALF_UP);

        // 品項銷售
        List<Long> soldIds = sold.stream().map(Order::getId).toList();
        List<OrderItem> items = soldIds.isEmpty() ? List.of() : orderItemRepository.findByOrderIdIn(soldIds);
        Map<Long, Product> products = productRepository.findAllById(
                items.stream().map(OrderItem::getProductId).filter(Objects::nonNull).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        Map<Long, long[]> quantityByProduct = new HashMap<>();
        Map<Long, BigDecimal> revenueByProduct = new HashMap<>();
        for (OrderItem item : items) {
            if (item.getProductId() == null) {
                continue;
            }
            quantityByProduct.computeIfAbsent(item.getProductId(), id -> new long[1])[0] +=
                    item.getQuantity() == null ? 0 : item.getQuantity();
            revenueByProduct.merge(item.getProductId(), itemRevenue(item), BigDecimal::add);
        }
        List<StatisticsOverviewDTO.ProductSales> topProducts = quantityByProduct.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
                .limit(10)
                .map(entry -> StatisticsOverviewDTO.ProductSales.builder()
                        .id(entry.getKey())
                        .name(Optional.ofNullable(products.get(entry.getKey())).map(Product::getName)
                                .orElseGet(() -> items.stream().filter(i -> entry.getKey().equals(i.getProductId()))
                                        .map(OrderItem::getProductName).filter(Objects::nonNull).findFirst().orElse("已刪除的商品")))
                        .sales(entry.getValue()[0])
                        .revenue(revenueByProduct.getOrDefault(entry.getKey(), BigDecimal.ZERO))
                        .build())
                .toList();

        // 分類銷售
        Map<Long, String> categoryNames = productCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(ProductCategory::getId, ProductCategory::getName, (a, b) -> a));
        Map<Long, long[]> quantityByCategory = new HashMap<>();
        Map<Long, BigDecimal> revenueByCategory = new HashMap<>();
        for (Map.Entry<Long, long[]> entry : quantityByProduct.entrySet()) {
            Long categoryId = Optional.ofNullable(products.get(entry.getKey())).map(Product::getCategoryId).orElse(0L);
            quantityByCategory.computeIfAbsent(categoryId, id -> new long[1])[0] += entry.getValue()[0];
            revenueByCategory.merge(categoryId, revenueByProduct.getOrDefault(entry.getKey(), BigDecimal.ZERO), BigDecimal::add);
        }
        List<StatisticsOverviewDTO.CategorySales> topCategories = quantityByCategory.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
                .limit(10)
                .map(entry -> StatisticsOverviewDTO.CategorySales.builder()
                        .id(entry.getKey())
                        .name(categoryNames.getOrDefault(entry.getKey(), "未分類"))
                        .sales(entry.getValue()[0])
                        .revenue(revenueByCategory.get(entry.getKey()))
                        .build())
                .toList();

        // 每日趨勢（沒有訂單的日子補 0）
        Map<LocalDate, List<Order>> byDay = sold.stream()
                .collect(Collectors.groupingBy(order -> order.getCreatedAt().toLocalDate()));
        List<StatisticsOverviewDTO.DailySales> trend = new ArrayList<>();
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            List<Order> dayOrders = byDay.getOrDefault(day, List.of());
            trend.add(StatisticsOverviewDTO.DailySales.builder()
                    .date(day.toString())
                    .sales(sum(dayOrders, Order::getTotalAmount))
                    .orders(dayOrders.size())
                    .build());
        }

        // 訂單狀態（期間內所有訂單）
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            statusCounts.put(status.getDescription(), orders.stream().filter(order -> order.getStatus() == status).count());
        }

        // 付款方式：前台訂單依結帳時選擇，其他為後台建立
        Map<Long, String> methodByOrder = soldIds.isEmpty() ? Map.of()
                : orderHistoryRepository.findByOrderIdInAndActionType(soldIds, StorefrontCheckoutService.ACTION_STOREFRONT_CHECKOUT)
                        .stream()
                        .collect(Collectors.toMap(OrderHistory::getOrderId, OrderHistory::getNewStatus, (a, b) -> a));
        Map<String, Long> paymentMethods = sold.stream()
                .collect(Collectors.groupingBy(order -> paymentLabel(methodByOrder.get(order.getId())),
                        LinkedHashMap::new, Collectors.counting()));

        return StatisticsOverviewDTO.builder()
                .startDate(start.toString())
                .endDate(end.toString())
                .totalSales(totalSales)
                .totalOrders(sold.size())
                .totalCustomers(customers)
                .newCustomers(memberRepository.countByCreatedAtBetween(from, to))
                .averageOrderValue(average)
                .refundAmount(orderHistoryRepository.refundAmountsBetween(from, to).stream().reduce(BigDecimal.ZERO, BigDecimal::add))
                .topProducts(topProducts)
                .topCategories(topCategories)
                .salesTrend(trend)
                .orderStatus(statusCounts)
                .paymentMethods(paymentMethods)
                .build();
    }

    private static String paymentLabel(String method) {
        if (StorefrontCheckoutService.PAYMENT_ECPAY.equals(method)) {
            return "線上付款（綠界）";
        }
        if (StorefrontCheckoutService.PAYMENT_COD.equals(method)) {
            return "貨到付款";
        }
        return "後台建立";
    }

    private static BigDecimal itemRevenue(OrderItem item) {
        if (item.getSubtotalAmount() != null) {
            return item.getSubtotalAmount();
        }
        BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(item.getQuantity() == null ? 0 : item.getQuantity()));
    }

    private static BigDecimal sum(List<Order> orders, Function<Order, BigDecimal> getter) {
        return orders.stream().map(getter).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
