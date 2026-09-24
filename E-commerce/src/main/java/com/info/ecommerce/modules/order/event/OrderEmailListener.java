package com.info.ecommerce.modules.order.event;

import com.info.ecommerce.modules.order.dto.OrderDTO;
import com.info.ecommerce.modules.order.dto.OrderItemDTO;
import com.info.ecommerce.modules.order.enums.NotificationType;
import com.info.ecommerce.modules.order.enums.PickupType;
import com.info.ecommerce.modules.order.service.OrderNotificationService;
import com.info.ecommerce.modules.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 寄送顧客訂單通知信：下單成功、付款完成、訂單取消。
 * 在交易提交後以非同步方式寄送，寄信失敗不影響訂單流程；未設定郵件伺服器或訂單沒有 Email 時略過。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEmailListener {

    private final OrderService orderService;
    private final OrderNotificationService orderNotificationService;

    @Value("${app.mail.order-notifications:true}")
    private boolean enabled;

    @Value("${app.mail.store-name:遇日小舖}")
    private String storeName;

    @Value("${app.storefront-url:}")
    private String storefrontUrl;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderEmail(OrderEmailEvent event) {
        if (!enabled || !orderNotificationService.isEmailEnabled()) {
            return;
        }
        try {
            OrderDTO order = orderService.getOrder(event.orderId());
            if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) {
                return;
            }
            orderNotificationService.sendNotification(order.getId(), NotificationType.EMAIL,
                    order.getCustomerEmail(), subject(event.type(), order), body(event.type(), order));
        } catch (Exception e) {
            log.error("Failed to send order email {} for order {}", event.type(), event.orderId(), e);
        }
    }

    String subject(OrderEmailEvent.Type type, OrderDTO order) {
        return switch (type) {
            case CREATED -> "【" + storeName + "】訂單成立通知 " + order.getOrderNumber();
            case PAID -> "【" + storeName + "】付款完成通知 " + order.getOrderNumber();
            case CANCELLED -> "【" + storeName + "】訂單取消通知 " + order.getOrderNumber();
        };
    }

    String body(OrderEmailEvent.Type type, OrderDTO order) {
        StringBuilder sb = new StringBuilder();
        sb.append(order.getCustomerName() != null ? order.getCustomerName() : "顧客").append(" 您好：\n\n");
        switch (type) {
            case CREATED -> {
                sb.append("感謝您在").append(storeName).append("訂購，我們已收到您的訂單。\n");
                if (order.getNotes() != null && order.getNotes().contains("線上付款")) {
                    sb.append("若尚未完成線上付款，請透過下方「查詢訂單」連結前往付款；逾期未付款的訂單將自動取消。\n");
                }
            }
            case PAID -> sb.append("我們已收到您的付款，將盡快為您安排出貨。\n");
            case CANCELLED -> sb.append("您的訂單已取消。如已付款，我們將與您聯繫退款事宜；如有疑問請直接回覆此信或聯繫客服。\n");
        }
        sb.append("\n訂單編號：").append(order.getOrderNumber()).append('\n');
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            sb.append("\n訂購商品：\n");
            for (OrderItemDTO item : order.getItems()) {
                sb.append("・").append(item.getProductName());
                if (item.getProductSpec() != null && !item.getProductSpec().isBlank()) {
                    sb.append("（").append(item.getProductSpec()).append("）");
                }
                sb.append(" × ").append(item.getQuantity())
                        .append("　NT$ ").append(money(item.getSubtotalAmount())).append('\n');
            }
        }
        sb.append("\n商品小計：NT$ ").append(money(order.getSubtotalAmount())).append('\n');
        sb.append("運費：NT$ ").append(money(order.getShippingFee())).append('\n');
        if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            sb.append("折扣：-NT$ ").append(money(order.getDiscountAmount())).append('\n');
        }
        sb.append("訂單總額：NT$ ").append(money(order.getTotalAmount())).append('\n');
        sb.append("配送方式：").append(order.getPickupType() == PickupType.STORE_PICKUP ? "門市自取" : "宅配到府").append('\n');
        if (order.getShippingAddress() != null && !order.getShippingAddress().isBlank()) {
            sb.append("收件地址：").append(order.getShippingAddress()).append('\n');
        }
        if (storefrontUrl != null && !storefrontUrl.isBlank()) {
            sb.append("\n查詢訂單：").append(storefrontUrl.replaceAll("/+$", ""))
                    .append("/shop/order/lookup?orderNumber=").append(encode(order.getOrderNumber()))
                    .append("&email=").append(encode(order.getCustomerEmail())).append('\n');
        }
        sb.append("\n").append(storeName).append(" 敬上\n（此信件由系統自動發送）\n");
        return sb.toString();
    }

    private static String money(BigDecimal value) {
        return value == null ? "0" : value.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
