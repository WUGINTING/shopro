package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.marketing.repository.CouponRepository;
import com.info.ecommerce.modules.marketing.service.CheckoutDiscountService;
import com.info.ecommerce.modules.order.entity.OrderDiscount;
import com.info.ecommerce.modules.order.repository.OrderDiscountRepository;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 訂單使用的優惠券次數：下單時扣除，訂單取消時歸還，取消的訂單被還原時再扣除。
 * 以訂單歷程 COUPON_USED / COUPON_RELEASED 的筆數判斷目前是否佔用，重複呼叫不會重複增減。
 */
@Service
@RequiredArgsConstructor
public class OrderCouponService {

    public static final String ACTION_USED = "COUPON_USED";
    public static final String ACTION_RELEASED = "COUPON_RELEASED";

    private final CouponRepository couponRepository;
    private final OrderDiscountRepository orderDiscountRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderHistoryService orderHistoryService;

    /** 訂單取消：歸還優惠券使用次數 */
    @Transactional
    public void release(Long orderId) {
        String code = couponCode(orderId);
        if (code == null || !holdsCoupon(orderId)) {
            return;
        }
        couponRepository.findByCode(code).ifPresent(coupon -> couponRepository.decrementUsage(coupon.getId()));
        orderHistoryService.recordHistory(orderId, ACTION_RELEASED, "訂單取消，歸還優惠券 " + code, null, code, null, "系統");
    }

    /** 已取消的訂單被還原：重新計入優惠券使用次數 */
    @Transactional
    public void reclaim(Long orderId) {
        String code = couponCode(orderId);
        long used = orderHistoryRepository.countByOrderIdAndActionType(orderId, ACTION_USED);
        if (code == null || used == 0 || holdsCoupon(orderId)) {
            return;
        }
        couponRepository.findByCode(code).ifPresent(coupon -> couponRepository.forceIncrementUsage(coupon.getId()));
        orderHistoryService.recordHistory(orderId, ACTION_USED, "訂單恢復，重新使用優惠券 " + code, null, code, null, "系統");
    }

    private boolean holdsCoupon(Long orderId) {
        return orderHistoryRepository.countByOrderIdAndActionType(orderId, ACTION_USED)
                > orderHistoryRepository.countByOrderIdAndActionType(orderId, ACTION_RELEASED);
    }

    /** 優惠券代碼以使用記錄為準（後台修改折扣明細後仍找得到）；舊資料沒有記錄代碼時改看折扣明細 */
    private String couponCode(Long orderId) {
        String recorded = orderHistoryRepository.findByOrderIdAndActionType(orderId, ACTION_USED).stream()
                .map(com.info.ecommerce.modules.order.entity.OrderHistory::getNewStatus)
                .filter(code -> code != null && !code.isBlank())
                .findFirst()
                .orElse(null);
        if (recorded != null) {
            return recorded;
        }
        return orderDiscountRepository.findByOrderId(orderId).stream()
                .filter(discount -> discount.getDiscountCode() != null
                        && (CheckoutDiscountService.TYPE_COUPON.equals(discount.getDiscountType())
                            || CheckoutDiscountService.TYPE_FREE_SHIPPING.equals(discount.getDiscountType())))
                .map(OrderDiscount::getDiscountCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
