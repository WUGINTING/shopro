package com.info.ecommerce.modules.order.event;

/**
 * 需要寄送顧客通知信的訂單事件（交易提交後才會寄送）
 *
 * @param orderId 訂單 ID
 * @param type    通知類型
 */
public record OrderEmailEvent(Long orderId, Type type) {

    public enum Type {
        /** 前台下單成功 */
        CREATED,
        /** 付款完成 */
        PAID,
        /** 訂單取消 */
        CANCELLED
    }
}
