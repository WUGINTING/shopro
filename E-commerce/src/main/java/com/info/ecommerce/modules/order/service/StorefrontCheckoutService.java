package com.info.ecommerce.modules.order.service;

import com.info.ecommerce.modules.auth.service.CurrentUserService;
import com.info.ecommerce.modules.marketing.repository.CouponRepository;
import com.info.ecommerce.modules.marketing.service.CheckoutDiscountService;
import com.info.ecommerce.modules.order.entity.OrderDiscount;
import com.info.ecommerce.modules.order.repository.OrderDiscountRepository;

import com.info.ecommerce.modules.order.event.OrderEmailEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.info.ecommerce.common.exception.BusinessException;
import com.info.ecommerce.modules.crm.entity.Member;
import com.info.ecommerce.modules.crm.repository.MemberRepository;
import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.OrderItemDTO;
import com.info.ecommerce.modules.order.dto.StorefrontCheckoutRequest;
import com.info.ecommerce.modules.order.dto.StorefrontCheckoutResultDTO;
import com.info.ecommerce.modules.order.dto.StorefrontQuoteDTO;
import com.info.ecommerce.modules.order.entity.Order;
import com.info.ecommerce.modules.order.enums.OrderStatus;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.dto.StorefrontOrderLookupDTO;
import com.info.ecommerce.modules.order.entity.OrderHistory;
import com.info.ecommerce.modules.order.repository.OrderHistoryRepository;
import com.info.ecommerce.modules.order.repository.OrderRepository;
import com.info.ecommerce.modules.payment.dto.PaymentRequestDTO;
import com.info.ecommerce.modules.payment.dto.PaymentResponseDTO;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.enums.PaymentGatewayStatus;
import com.info.ecommerce.modules.payment.service.PaymentGatewayFactory;
import com.info.ecommerce.modules.product.entity.Product;
import com.info.ecommerce.modules.product.entity.ProductSpecification;
import com.info.ecommerce.modules.product.enums.ProductStatus;
import com.info.ecommerce.modules.product.repository.ProductInventoryRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.product.repository.ProductSpecificationRepository;
import com.info.ecommerce.modules.system.entity.ShippingConfig;
import com.info.ecommerce.modules.system.repository.ShippingConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 前台結帳服務
 * 提供訪客結帳（不需登入）：試算、建立訂單、以訂單編號 + Email 查詢訂單。
 * 所有金額皆以後端商品/規格/物流設定重新計算，不信任前端傳入的價格。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorefrontCheckoutService {

    public static final String SHIPPING_HOME_DELIVERY = "HOME_DELIVERY";
    public static final String SHIPPING_STORE_PICKUP = "STORE_PICKUP";
    public static final String PAYMENT_ECPAY = "ECPAY";
    public static final String PAYMENT_COD = "COD";

    /** 後台未設定宅配物流時的預設運費規則（與前台原本顯示一致：運費 100、滿 1000 免運） */
    static final BigDecimal DEFAULT_HOME_DELIVERY_FEE = new BigDecimal("100");
    static final BigDecimal DEFAULT_FREE_SHIPPING_THRESHOLD = new BigDecimal("1000");

    private final ProductRepository productRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final ShippingConfigRepository shippingConfigRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentGatewayFactory paymentGatewayFactory;
    private final OrderStockService orderStockService;
    private final OrderHistoryService orderHistoryService;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CheckoutDiscountService checkoutDiscountService;
    private final CurrentUserService currentUserService;
    private final OrderDiscountRepository orderDiscountRepository;
    private final CouponRepository couponRepository;

    @Value("${app.storefront-url:}")
    private String storefrontUrl;

    @Value("${app.admin-store-url:}")
    private String adminStoreUrl;

    /**
     * 寫入訂單折扣紀錄並扣除優惠券使用次數（次數用完時整筆訂單回滾）
     */
    private void recordDiscounts(Long orderId, CheckoutDiscountService.Result discount) {
        for (CheckoutDiscountService.Applied applied : discount.applied()) {
            orderDiscountRepository.save(OrderDiscount.builder()
                    .orderId(orderId)
                    .discountType(applied.type())
                    .discountCode(applied.code())
                    .discountAmount(applied.amount())
                    .description(applied.name())
                    .build());
        }
        if (discount.couponId() != null) {
            if (couponRepository.incrementUsage(discount.couponId()) == 0) {
                throw new BusinessException("此優惠券已被使用完畢");
            }
            orderHistoryService.recordHistory(orderId, OrderCouponService.ACTION_USED,
                    "使用優惠券 " + discount.couponCode(), null, null, null, "顧客");
        }
    }

    /** 訂單歷程動作：前台結帳（newStatus 欄位記錄付款方式 ECPAY / COD，供逾期未付款清理判斷） */
    public static final String ACTION_STOREFRONT_CHECKOUT = "STOREFRONT_CHECKOUT";

    /** 訂單歷程動作：建立線上付款（逾期清理從最後一次付款請求起算） */
    public static final String ACTION_PAYMENT_REQUESTED = "PAYMENT_REQUESTED";

    /**
     * 結帳試算：驗證品項並計算小計、運費與總額
     */
    @Transactional(readOnly = true)
    public StorefrontQuoteDTO quote(List<StorefrontCheckoutRequest.Item> items, String shippingMethod) {
        return quote(items, shippingMethod, null);
    }

    /**
     * 結帳試算（含優惠券）：無效的優惠券不影響試算，只在 couponMessage 說明原因
     */
    @Transactional(readOnly = true)
    public StorefrontQuoteDTO quote(List<StorefrontCheckoutRequest.Item> items, String shippingMethod, String couponCode) {
        return price(items, shippingMethod, couponCode, false).quote();
    }

    /** 試算結果與折扣計算明細（下單時需要知道要扣哪張優惠券） */
    private record Pricing(StorefrontQuoteDTO quote, CheckoutDiscountService.Result discount) {}

    private Pricing price(List<StorefrontCheckoutRequest.Item> items, String shippingMethod, String couponCode, boolean strictCoupon) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException("購物車是空的");
        }

        String method = normalizeShippingMethod(shippingMethod);
        List<StorefrontQuoteDTO.Line> lines = new ArrayList<>();
        // 同一商品/規格若重複出現，合併數量後再檢查庫存
        Map<String, Integer> requestedQuantities = new HashMap<>();
        // 購買數量限制以「商品」為單位（不同規格合計）
        Map<Long, Integer> productQuantities = new java.util.LinkedHashMap<>();
        Map<Long, Product> productsById = new HashMap<>();

        for (StorefrontCheckoutRequest.Item item : items) {
            if (item.getProductId() == null || item.getQuantity() == null || item.getQuantity() < 1) {
                throw new BusinessException("購物車品項資料不完整");
            }

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("商品不存在或已下架"));
            if (product.getStatus() != ProductStatus.ACTIVE || Boolean.FALSE.equals(product.getEnabled())) {
                throw new BusinessException("商品「" + product.getName() + "」目前無法購買");
            }

            String key = item.getProductId() + ":" + item.getSpecificationId();
            int totalQuantity = requestedQuantities.merge(key, item.getQuantity(), Integer::sum);

            BigDecimal unitPrice;
            String specName = null;
            String sku = product.getSku();

            if (item.getSpecificationId() != null) {
                ProductSpecification spec = productSpecificationRepository.findById(item.getSpecificationId())
                        .orElseThrow(() -> new BusinessException("商品「" + product.getName() + "」的規格不存在"));
                if (!product.getId().equals(spec.getProductId())) {
                    throw new BusinessException("商品規格不屬於該商品");
                }
                if (Boolean.FALSE.equals(spec.getEnabled())) {
                    throw new BusinessException("商品「" + product.getName() + "」的規格「" + spec.getSpecName() + "」目前無法購買");
                }
                if (spec.getStock() != null && totalQuantity > spec.getStock()) {
                    throw new BusinessException("商品「" + product.getName() + " (" + spec.getSpecName() + ")」庫存不足，目前剩餘 "
                            + Math.max(spec.getStock(), 0) + " 件");
                }
                unitPrice = spec.getPrice() != null ? spec.getPrice() : productPrice(product);
                specName = spec.getSpecName();
                if (spec.getSku() != null && !spec.getSku().isEmpty()) {
                    sku = spec.getSku();
                }
            } else {
                if (!productSpecificationRepository.findByProductIdAndEnabledTrue(product.getId()).isEmpty()) {
                    throw new BusinessException("請選擇商品「" + product.getName() + "」的規格");
                }
                Integer available = productInventoryRepository.findByProductIdAndSpecificationId(product.getId(), null)
                        .map(inventory -> inventory.getAvailableStock())
                        .orElse(null);
                if (available != null && totalQuantity > available) {
                    throw new BusinessException("商品「" + product.getName() + "」庫存不足，目前剩餘 " + Math.max(available, 0) + " 件");
                }
                unitPrice = productPrice(product);
            }

            productQuantities.merge(product.getId(), item.getQuantity(), Integer::sum);
            productsById.putIfAbsent(product.getId(), product);

            lines.add(StorefrontQuoteDTO.Line.builder()
                    .productId(product.getId())
                    .specificationId(item.getSpecificationId())
                    .productName(product.getName())
                    .specName(specName)
                    .sku(sku)
                    .unitPrice(unitPrice)
                    .quantity(item.getQuantity())
                    .subtotalAmount(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build());
        }

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Product product = productsById.get(entry.getKey());
            int quantity = entry.getValue();
            if (product.getMinPurchaseQuantity() != null && quantity < product.getMinPurchaseQuantity()) {
                throw new BusinessException("商品「" + product.getName() + "」最少需購買 " + product.getMinPurchaseQuantity() + " 件");
            }
            if (product.getMaxPurchaseQuantity() != null && product.getMaxPurchaseQuantity() > 0
                    && quantity > product.getMaxPurchaseQuantity()) {
                throw new BusinessException("商品「" + product.getName() + "」每筆訂單最多購買 " + product.getMaxPurchaseQuantity() + " 件");
            }
        }

        BigDecimal subtotal = lines.stream()
                .map(StorefrontQuoteDTO.Line::getSubtotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShippingRule rule = resolveShippingRule(method);
        CheckoutDiscountService.Result discount = checkoutDiscountService.calculate(
                subtotal, couponCode, currentUserService.currentMember(), strictCoupon);
        BigDecimal shippingFee = discount.freeShipping()
                || (rule.threshold() != null && subtotal.compareTo(rule.threshold()) >= 0)
                ? BigDecimal.ZERO
                : rule.baseFee();

        StorefrontQuoteDTO quote = StorefrontQuoteDTO.builder()
                .lines(lines)
                .subtotalAmount(subtotal)
                .shippingFee(shippingFee)
                .freeShippingThreshold(rule.threshold())
                .discountAmount(discount.discountAmount())
                .discounts(discount.applied().stream()
                        .map(applied -> StorefrontQuoteDTO.Discount.builder()
                                .type(applied.type())
                                .name(applied.name())
                                .code(applied.code())
                                .amount(applied.amount())
                                .build())
                        .collect(Collectors.toList()))
                .couponCode(discount.couponCode())
                .couponMessage(discount.couponMessage())
                .totalAmount(subtotal.subtract(discount.discountAmount()).add(shippingFee))
                .shippingMethod(method)
                .build();
        return new Pricing(quote, discount);
    }

    /**
     * 訪客結帳：建立訂單，若選擇線上付款則同時建立綠界付款請求
     */
    @Transactional
    public StorefrontCheckoutResultDTO checkout(StorefrontCheckoutRequest request) {
        String shippingMethod = normalizeShippingMethod(request.getShippingMethod());
        String paymentMethod = normalizePaymentMethod(request.getPaymentMethod());

        if (SHIPPING_HOME_DELIVERY.equals(shippingMethod) && isBlank(request.getShippingAddress())) {
            throw new BusinessException("宅配到府請填寫收件地址");
        }

        Pricing pricing = price(request.getItems(), shippingMethod, request.getCouponCode(), true);
        StorefrontQuoteDTO quote = pricing.quote();
        Member member = findOrCreateMember(request);

        OrderDTO orderDTO = OrderDTO.builder()
                .customerId(member.getId())
                .customerName(request.getCustomerName().trim())
                .customerPhone(request.getCustomerPhone().trim())
                .customerEmail(normalizeEmail(request.getCustomerEmail()))
                .status(OrderStatus.PENDING_PAYMENT)
                .pickupType(SHIPPING_STORE_PICKUP.equals(shippingMethod) ? PickupType.STORE_PICKUP : PickupType.DELIVERY)
                .subtotalAmount(quote.getSubtotalAmount())
                .discountAmount(quote.getDiscountAmount())
                .shippingFee(quote.getShippingFee())
                .totalAmount(quote.getTotalAmount())
                .shippingAddress(SHIPPING_STORE_PICKUP.equals(shippingMethod) ? null : request.getShippingAddress().trim())
                .notes(buildNotes(request.getNotes(), paymentMethod, shippingMethod))
                .isDraft(false)
                .items(quote.getLines().stream()
                        .map(line -> OrderItemDTO.builder()
                                .productId(line.getProductId())
                                .specificationId(line.getSpecificationId())
                                .unitPrice(line.getUnitPrice())
                                .quantity(line.getQuantity())
                                .discountAmount(BigDecimal.ZERO)
                                .build())
                        .collect(Collectors.toList()))
                .build();

        OrderDTO order = orderService.createOrder(orderDTO);
        recordDiscounts(order.getId(), pricing.discount());
        // 扣庫存：庫存不足時丟出例外，整筆訂單回滾
        orderStockService.reserve(order.getId(), order.getOrderNumber());
        orderHistoryService.recordHistory(order.getId(), ACTION_STOREFRONT_CHECKOUT,
                "前台結帳：" + (PAYMENT_COD.equals(paymentMethod) ? "貨到付款" : "線上付款（綠界）"),
                null, paymentMethod, null, "顧客");
        eventPublisher.publishEvent(new OrderEmailEvent(order.getId(), OrderEmailEvent.Type.CREATED));

        StorefrontCheckoutResultDTO result = StorefrontCheckoutResultDTO.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .build();

        if (PAYMENT_ECPAY.equals(paymentMethod)) {
            createOnlinePayment(order, result, "ADMIN_STORE".equalsIgnoreCase(request.getChannel()));
        }

        return result;
    }

    /**
     * 訪客以訂單編號 + Email 查詢訂單
     */
    @Transactional(readOnly = true)
    public StorefrontOrderLookupDTO lookupOrder(String orderNumber, String email) {
        Order order = findOwnedOrder(orderNumber, email);
        String paymentMethod = storefrontPaymentMethod(order.getId());
        return StorefrontOrderLookupDTO.builder()
                .order(orderService.getOrder(order.getId()))
                .paymentMethod(paymentMethod)
                .canPayOnline(PAYMENT_ECPAY.equals(paymentMethod) && order.getStatus() == OrderStatus.PENDING_PAYMENT)
                .build();
    }

    /**
     * 待付款的線上付款訂單重新建立綠界付款（例如付款頁關閉或逾時）
     */
    @Transactional
    public StorefrontCheckoutResultDTO payAgain(String orderNumber, String email) {
        Order order = findOwnedOrder(orderNumber, email);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("訂單狀態為「" + order.getStatus().getDescription() + "」，無需付款");
        }
        if (!PAYMENT_ECPAY.equals(storefrontPaymentMethod(order.getId()))) {
            throw new BusinessException("此訂單為貨到付款，請於取貨時付款");
        }
        OrderDTO orderDTO = orderService.getOrder(order.getId());
        StorefrontCheckoutResultDTO result = StorefrontCheckoutResultDTO.builder()
                .order(orderDTO)
                .paymentMethod(PAYMENT_ECPAY)
                .build();
        createOnlinePayment(orderDTO, result, false);
        return result;
    }

    private Order findOwnedOrder(String orderNumber, String email) {
        if (isBlank(orderNumber) || isBlank(email)) {
            throw new BusinessException("請輸入訂單編號與電子郵件");
        }
        return orderRepository.findByOrderNumber(orderNumber.trim())
                .filter(o -> o.getCustomerEmail() != null
                        && o.getCustomerEmail().trim().equalsIgnoreCase(email.trim()))
                .orElseThrow(() -> new BusinessException("查無此訂單，請確認訂單編號與電子郵件是否正確"));
    }

    /** 前台結帳時記錄的付款方式（非前台訂單回傳 null） */
    private String storefrontPaymentMethod(Long orderId) {
        return orderHistoryRepository.findByOrderIdAndActionType(orderId, ACTION_STOREFRONT_CHECKOUT).stream()
                .map(OrderHistory::getNewStatus)
                .filter(method -> method != null && !method.isBlank())
                .findFirst()
                .orElse(null);
    }

    private void createOnlinePayment(OrderDTO order, StorefrontCheckoutResultDTO result, boolean adminStore) {
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            result.setPaymentError("訂單金額為 0，無需線上付款");
            return;
        }
        try {
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
            paymentRequest.setOrderId(order.getId());
            paymentRequest.setOrderNumber(order.getOrderNumber());
            paymentRequest.setAmount(order.getTotalAmount());
            paymentRequest.setCurrency("TWD");
            paymentRequest.setProductName(buildPaymentItemName(order));
            paymentRequest.setCustomerName(order.getCustomerName());
            paymentRequest.setCustomerEmail(order.getCustomerEmail());
            paymentRequest.setCustomerPhone(order.getCustomerPhone());
            if (adminStore && !isBlank(adminStoreUrl)) {
                // 後台 App 的顧客商城：回到該 App 的訂單完成頁
                paymentRequest.setClientBackUrl(adminStoreUrl.replaceAll("/+$", "")
                        + "/order/success?orderNumber=" + order.getOrderNumber());
            } else if (!isBlank(storefrontUrl)) {
                // 付款完成後「返回商店」回到前台訂單完成頁（完成頁會查詢最新付款狀態）
                paymentRequest.setClientBackUrl(storefrontUrl.replaceAll("/+$", "")
                        + "/shop/order/success?orderNumber=" + order.getOrderNumber());
            }

            PaymentResponseDTO response = paymentGatewayFactory
                    .getPaymentGatewayService(PaymentGateway.ECPAY)
                    .createPayment(paymentRequest);

            if (response == null || response.getStatus() == PaymentGatewayStatus.FAILED || isBlank(response.getPaymentUrl())) {
                result.setPaymentError(response != null && response.getErrorMessage() != null
                        ? response.getErrorMessage() : "建立線上付款失敗");
            } else {
                result.setPaymentUrl(response.getPaymentUrl());
                orderHistoryService.recordHistory(order.getId(), ACTION_PAYMENT_REQUESTED, "建立綠界線上付款",
                        null, null, null, "顧客");
            }
        } catch (Exception e) {
            // 付款建立失敗不回滾訂單：訂單保持待付款，客人可稍後再付款或聯繫客服
            log.error("Failed to create ECPay payment for order {}", order.getOrderNumber(), e);
            result.setPaymentError("建立線上付款失敗，訂單已保留為待付款狀態");
        }
    }

    private Member findOrCreateMember(StorefrontCheckoutRequest request) {
        String email = normalizeEmail(request.getCustomerEmail());
        return memberRepository.findByEmail(email).orElseGet(() -> memberRepository.save(Member.builder()
                .name(request.getCustomerName().trim())
                .email(email)
                .phone(request.getCustomerPhone().trim())
                .address(isBlank(request.getShippingAddress()) ? null : request.getShippingAddress().trim())
                .notes("前台結帳自動建立")
                .build()));
    }

    private ShippingRule resolveShippingRule(String shippingMethod) {
        List<ShippingConfig> configs = shippingConfigRepository.findByEnabledOrderBySortOrderAsc(true);
        for (ShippingConfig config : configs) {
            if (shippingMethod.equalsIgnoreCase(config.getShippingMethod())) {
                BigDecimal baseFee = config.getBaseShippingFee() != null ? config.getBaseShippingFee() : BigDecimal.ZERO;
                BigDecimal threshold = config.getFreeShippingThreshold() != null
                        && config.getFreeShippingThreshold().compareTo(BigDecimal.ZERO) > 0
                        ? config.getFreeShippingThreshold() : null;
                return new ShippingRule(baseFee, threshold);
            }
        }
        if (SHIPPING_STORE_PICKUP.equals(shippingMethod)) {
            return new ShippingRule(BigDecimal.ZERO, null);
        }
        return new ShippingRule(DEFAULT_HOME_DELIVERY_FEE, DEFAULT_FREE_SHIPPING_THRESHOLD);
    }

    private static BigDecimal productPrice(Product product) {
        if (product.getSalePrice() != null && product.getSalePrice().compareTo(BigDecimal.ZERO) > 0) {
            return product.getSalePrice();
        }
        if (product.getBasePrice() != null) {
            return product.getBasePrice();
        }
        throw new BusinessException("商品「" + product.getName() + "」尚未設定價格");
    }

    private static String normalizeShippingMethod(String shippingMethod) {
        if (isBlank(shippingMethod)) {
            return SHIPPING_HOME_DELIVERY;
        }
        String method = shippingMethod.trim().toUpperCase();
        if (!SHIPPING_HOME_DELIVERY.equals(method) && !SHIPPING_STORE_PICKUP.equals(method)) {
            throw new BusinessException("不支援的配送方式: " + shippingMethod);
        }
        return method;
    }

    private static String normalizePaymentMethod(String paymentMethod) {
        if (isBlank(paymentMethod)) {
            return PAYMENT_ECPAY;
        }
        String method = paymentMethod.trim().toUpperCase();
        if (!PAYMENT_ECPAY.equals(method) && !PAYMENT_COD.equals(method)) {
            throw new BusinessException("不支援的付款方式: " + paymentMethod);
        }
        return method;
    }

    private static String buildNotes(String customerNotes, String paymentMethod, String shippingMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append("[前台訂單] 付款方式：").append(PAYMENT_COD.equals(paymentMethod) ? "貨到付款" : "線上付款（綠界）");
        sb.append("；配送方式：").append(SHIPPING_STORE_PICKUP.equals(shippingMethod) ? "門市自取" : "宅配到府");
        if (!isBlank(customerNotes)) {
            sb.append("\n顧客備註：").append(customerNotes.trim());
        }
        return sb.toString();
    }

    private static String buildPaymentItemName(OrderDTO order) {
        // 綠界 CheckMacValue 目前未處理 ( ) ! * 等特殊字元的 .NET 編碼差異，品名維持純英數，與後台商城一致
        return "Shopro Order " + order.getOrderNumber();
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private record ShippingRule(BigDecimal baseFee, BigDecimal threshold) {
    }
}
