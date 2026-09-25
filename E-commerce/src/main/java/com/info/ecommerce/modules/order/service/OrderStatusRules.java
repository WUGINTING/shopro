package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.order.enums.OrderStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 訂單狀態可變更的方向：
 * - 待付款 → 已付款 / 處理中（貨到付款出貨）/ 已完成（現場收款）/ 已取消
 * - 已付款 → 處理中 / 已完成 / 已退款（已收款的訂單不可直接取消，請走退款）
 * - 處理中 → 已完成 / 已退款 / 已取消（例如貨到付款拒收）
 * - 已完成 → 已退款
 * - 已取消 → 待付款（誤取消時還原，會重新扣庫存）
 * - 已退款：不可再變更
 */
public final class OrderStatusRules {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED.put(OrderStatus.PENDING_PAYMENT,
                EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.COMPLETED, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.PAID, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.COMPLETED, OrderStatus.REFUNDED));
        ALLOWED.put(OrderStatus.PROCESSING, EnumSet.of(OrderStatus.COMPLETED, OrderStatus.REFUNDED, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.COMPLETED, EnumSet.of(OrderStatus.REFUNDED));
        ALLOWED.put(OrderStatus.CANCELLED, EnumSet.of(OrderStatus.PENDING_PAYMENT));
        ALLOWED.put(OrderStatus.REFUNDED, EnumSet.noneOf(OrderStatus.class));
    }

    private OrderStatusRules() {
    }

    public static boolean canChange(OrderStatus from, OrderStatus to) {
        return from == null || to == null || from == to || ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    public static Set<OrderStatus> nextStatuses(OrderStatus from) {
        return ALLOWED.getOrDefault(from, Set.of());
    }

    public static void assertCanChange(OrderStatus from, OrderStatus to) {
        if (canChange(from, to)) {
            return;
        }
        if (from == OrderStatus.PAID && to == OrderStatus.CANCELLED) {
            throw new BusinessException("已付款的訂單不可直接取消，請使用「退款」處理");
        }
        if (from == OrderStatus.REFUNDED) {
            throw new BusinessException("已退款的訂單不可再變更狀態");
        }
        throw new BusinessException("訂單狀態不可從「" + from.getDescription() + "」改為「" + to.getDescription() + "」");
    }
}
