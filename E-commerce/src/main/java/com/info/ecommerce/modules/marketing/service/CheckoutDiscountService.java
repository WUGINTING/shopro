package com.info.ecommerce.modules.marketing.service;

import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.entity.MemberLevel;
import com.info.ecommerce.modules.crm.repository.MemberLevelRepository;
import com.info.ecommerce.modules.marketing.entity.Coupon;
import com.info.ecommerce.modules.marketing.entity.Promotion;
import com.info.ecommerce.modules.marketing.repository.CouponRepository;
import com.info.ecommerce.modules.marketing.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 結帳折扣計算
 *
 * 規則（與前台「系統自動套用最優惠方案」說明一致）：
 * - 金額折扣只取一個最優惠的來源：進行中的促銷活動（DISCOUNT / FULL_SHOP）、優惠券、會員等級折扣，三者取折抵最多者，不累加
 * - 免運另外計算：符合免運促銷（FREE_SHIPPING）或使用免運券時運費為 0，可與金額折扣並用
 * - 折扣不超過商品小計；新台幣金額四捨五入到整數
 * - 優惠券只有實際被套用（折抵最多，或提供免運）時才算使用一次
 */
@Service
@RequiredArgsConstructor
public class CheckoutDiscountService {

    public static final String TYPE_PROMOTION = "PROMOTION";
    public static final String TYPE_COUPON = "COUPON";
    public static final String TYPE_MEMBER_LEVEL = "MEMBER_LEVEL";
    public static final String TYPE_FREE_SHIPPING = "FREE_SHIPPING";

    private final PromotionRepository promotionRepository;
    private final CouponRepository couponRepository;
    private final MemberLevelRepository memberLevelRepository;

    /** 一筆套用的折扣；免運時 amount 為 0 */
    public record Applied(String type, String name, String code, BigDecimal amount) {}

    /**
     * @param discountAmount 商品折扣金額
     * @param freeShipping 是否免運
     * @param applied 套用的折扣明細（顯示與寫入訂單折扣紀錄）
     * @param couponId 實際套用的優惠券（要扣使用次數），未套用為 null
     * @param couponMessage 優惠券未套用的原因（代碼無效、未達門檻、已有更優惠方案…）
     */
    public record Result(BigDecimal discountAmount, boolean freeShipping, List<Applied> applied,
                         Long couponId, String couponCode, String couponMessage) {}

    /**
     * 計算折扣
     * @param subtotal 商品小計
     * @param couponCode 顧客輸入的優惠券代碼（可空）
     * @param member 已登入且 Email 已驗證的會員（用於會員等級折扣），訪客為 empty
     * @param strictCoupon true 時優惠券無效直接丟出錯誤（正式結帳），false 時只回傳訊息（試算）
     */
    public Result calculate(BigDecimal subtotal, String couponCode, Optional<Member> member, boolean strictCoupon) {
        return calculate(subtotal, couponCode, member, strictCoupon, BigDecimal.ONE);
    }

    /**
     * @param shippingFee 套用免運前的運費（已考慮免運門檻與門市自取）；為 0 時免運優惠沒有作用，免運券不會被使用
     */
    public Result calculate(BigDecimal subtotal, String couponCode, Optional<Member> member, boolean strictCoupon,
                            BigDecimal shippingFee) {
        boolean shippingCharged = shippingFee != null && shippingFee.signum() > 0;
        LocalDate today = LocalDate.now();
        List<Promotion> promotions = promotionRepository.findCurrent(today, Pageable.unpaged()).getContent();

        List<Applied> candidates = new ArrayList<>();
        Applied freeShippingSource = null;

        for (Promotion promotion : promotions.stream()
                .sorted(Comparator.comparing((Promotion p) -> p.getPriority() == null ? 0 : p.getPriority()).reversed())
                .toList()) {
            if (!meetsMinimum(subtotal, promotion.getMinPurchaseAmount())) {
                continue;
            }
            String type = promotion.getType() == null ? "" : promotion.getType();
            if ("FREE_SHIPPING".equals(type)) {
                if (freeShippingSource == null) {
                    freeShippingSource = new Applied(TYPE_FREE_SHIPPING, promotion.getName(), null, BigDecimal.ZERO);
                }
            } else if (("DISCOUNT".equals(type) || "FULL_SHOP".equals(type)) && isValidDiscount(promotion)) {
                BigDecimal amount = discountOf(subtotal, promotion.getDiscountType(), promotion.getDiscountValue(),
                        promotion.getMaxDiscountAmount());
                if (amount.signum() > 0) {
                    candidates.add(new Applied(TYPE_PROMOTION, promotion.getName(), null, amount));
                }
            }
        }

        member.flatMap(this::memberLevelDiscount).map(level -> {
            BigDecimal rate = level.getDiscountRate();
            BigDecimal amount = subtotal.multiply(BigDecimal.ONE.subtract(rate)).setScale(0, RoundingMode.HALF_UP);
            return new Applied(TYPE_MEMBER_LEVEL, level.getName() + "會員折扣", null, amount);
        }).filter(applied -> applied.amount().signum() > 0).ifPresent(candidates::add);

        // 優惠券
        Coupon coupon = null;
        Applied couponCandidate = null;
        String couponMessage = null;
        String normalizedCode = CouponService.normalizeCode(couponCode);
        if (normalizedCode != null && !normalizedCode.isEmpty()) {
            Optional<Coupon> found = couponRepository.findByCode(normalizedCode);
            couponMessage = found.isEmpty() ? "優惠券代碼不存在" : invalidReason(found.get(), subtotal, today);
            if (couponMessage == null) {
                coupon = found.get();
                if ("FREE_SHIPPING".equals(coupon.getType())) {
                    couponCandidate = new Applied(TYPE_FREE_SHIPPING, coupon.getName(), coupon.getCode(), BigDecimal.ZERO);
                } else {
                    couponCandidate = new Applied(TYPE_COUPON, coupon.getName(), coupon.getCode(),
                            discountOf(subtotal, coupon.getType(), coupon.getDiscountValue(), coupon.getMaxDiscountAmount()));
                    candidates.add(couponCandidate);
                }
            } else if (strictCoupon) {
                throw new BusinessException(couponMessage);
            }
        }

        // 金額折扣取最優惠者（同額時促銷優先，其次會員等級，最後優惠券，以免白白消耗優惠券）
        Applied best = candidates.stream()
                .max(Comparator.comparing(Applied::amount)
                        .thenComparing(applied -> TYPE_COUPON.equals(applied.type()) ? 0 : 1))
                .orElse(null);

        List<Applied> applied = new ArrayList<>();
        BigDecimal discount = BigDecimal.ZERO;
        if (best != null) {
            discount = best.amount().min(subtotal);
            applied.add(new Applied(best.type(), best.name(), best.code(), discount));
        }

        boolean couponUsed = false;
        if (couponCandidate != null) {
            if (TYPE_FREE_SHIPPING.equals(couponCandidate.type())) {
                if (!shippingCharged) {
                    couponMessage = "本訂單已免運，不需使用此優惠券";
                } else if (freeShippingSource == null) {
                    freeShippingSource = couponCandidate;
                    couponUsed = true;
                } else {
                    couponMessage = "本訂單已符合免運，不需使用此優惠券";
                }
            } else if (best == couponCandidate) {
                couponUsed = true;
            } else {
                couponMessage = "已自動套用更優惠的方案，此優惠券本次不會使用";
            }
        }
        // 免運只在實際有運費時才列為套用的優惠
        if (freeShippingSource != null && shippingCharged) {
            applied.add(freeShippingSource);
        }

        return new Result(discount, freeShippingSource != null && shippingCharged, applied,
                couponUsed ? coupon.getId() : null, couponUsed ? coupon.getCode() : null, couponMessage);
    }

    private Optional<MemberLevel> memberLevelDiscount(Member member) {
        if (member.getLevelId() == null) {
            return Optional.empty();
        }
        return memberLevelRepository.findById(member.getLevelId())
                .filter(level -> !Boolean.FALSE.equals(level.getEnabled()))
                .filter(level -> level.getDiscountRate() != null
                        && level.getDiscountRate().signum() > 0
                        && level.getDiscountRate().compareTo(BigDecimal.ONE) < 0);
    }

    private static String invalidReason(Coupon coupon, BigDecimal subtotal, LocalDate today) {
        if (!Boolean.TRUE.equals(coupon.getEnabled())) {
            return "此優惠券已停用";
        }
        if (coupon.getValidFrom() != null && today.isBefore(coupon.getValidFrom())) {
            return "此優惠券尚未開始使用（" + coupon.getValidFrom() + " 起）";
        }
        if (coupon.getValidUntil() != null && today.isAfter(coupon.getValidUntil())) {
            return "此優惠券已過期";
        }
        if (coupon.getTotalCount() != null && coupon.getUsedCount() != null
                && coupon.getUsedCount() >= coupon.getTotalCount()) {
            return "此優惠券已被使用完畢";
        }
        if (!meetsMinimum(subtotal, coupon.getMinPurchaseAmount())) {
            return "商品金額需滿 NT$" + coupon.getMinPurchaseAmount().stripTrailingZeros().toPlainString() + " 才能使用此優惠券";
        }
        return null;
    }

    /** 舊資料可能沒有折扣方式或百分比不合理（例如 100%），這類活動只作為說明，不套用 */
    private static boolean isValidDiscount(Promotion promotion) {
        BigDecimal value = promotion.getDiscountValue();
        if (value == null || value.signum() <= 0) {
            return false;
        }
        if ("PERCENTAGE".equals(promotion.getDiscountType())) {
            return value.compareTo(BigDecimal.valueOf(100)) < 0;
        }
        return "FIXED".equals(promotion.getDiscountType());
    }

    private static boolean meetsMinimum(BigDecimal subtotal, BigDecimal minimum) {
        return minimum == null || subtotal.compareTo(minimum) >= 0;
    }

    /** PERCENTAGE：value 為折扣百分比（10 = 打 9 折）；FIXED：折抵金額 */
    private static BigDecimal discountOf(BigDecimal subtotal, String discountType, BigDecimal value, BigDecimal cap) {
        if (value == null || value.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal amount;
        if ("PERCENTAGE".equals(discountType)) {
            BigDecimal percent = value.min(BigDecimal.valueOf(100));
            amount = subtotal.multiply(percent).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        } else {
            amount = value.setScale(0, RoundingMode.HALF_UP);
        }
        if (cap != null && cap.signum() > 0) {
            amount = amount.min(cap);
        }
        return amount.min(subtotal).max(BigDecimal.ZERO);
    }
}
