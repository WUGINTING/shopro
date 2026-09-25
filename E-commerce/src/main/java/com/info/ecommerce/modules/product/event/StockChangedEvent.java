package com.info.ecommerce.modules.product.event;

import java.util.Collection;

/**
 * 商品庫存變動（交易提交後處理低庫存警示與到貨通知）
 *
 * @param productIds   變動的商品
 * @param replenished  是否有增加庫存（補貨、訂單取消歸還），需要檢查到貨通知
 */
public record StockChangedEvent(Collection<Long> productIds, boolean replenished) {
}
