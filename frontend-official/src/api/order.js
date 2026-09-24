import http from '@/utils/request';

/**
 * 前台訂單 API（訪客結帳，不需登入）
 */

/**
 * 將購物車項目轉為後端需要的品項格式（價格一律由後端計算）
 * @param {Array} cartItems - 購物車項目
 * @returns {Array<{productId: number, specificationId: number|null, quantity: number}>}
 */
export function toOrderItems(cartItems = []) {
  return cartItems.map(item => ({
    productId: item.id,
    specificationId: item.specification?.id || null,
    quantity: item.quantity,
  }));
}

/**
 * 結帳試算（小計、運費、總額，並檢查庫存）
 * @param {Object} data
 * @param {Array} data.items - 品項 [{ productId, specificationId, quantity }]
 * @param {string} data.shippingMethod - HOME_DELIVERY / STORE_PICKUP
 * @returns {Promise} ApiResponse<StorefrontQuoteDTO>
 */
export function quoteOrder(data) {
  // 錯誤訊息由結帳頁面內嵌顯示，不另外跳出通知
  return http.post('/storefront/orders/quote', data, { silent: true });
}

/**
 * 訪客結帳，建立訂單
 * @param {Object} data - StorefrontCheckoutRequest
 * @returns {Promise} ApiResponse<StorefrontCheckoutResultDTO>
 */
export function checkoutOrder(data) {
  return http.post('/storefront/orders/checkout', data);
}

/**
 * 以訂單編號 + 電子郵件查詢訂單
 * @param {string} orderNumber - 訂單編號
 * @param {string} email - 下單時填寫的電子郵件
 * @returns {Promise} ApiResponse<OrderDTO>
 */
export function lookupOrder(orderNumber, email) {
  return http.get('/storefront/orders/lookup', { orderNumber, email }, { silent: true });
}
