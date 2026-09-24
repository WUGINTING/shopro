// 結帳共用方法

const CHECKOUT_DRAFT_KEY = 'shop_checkout_draft';
const LAST_ORDER_KEY = 'shop_last_order';

function readJson(storage, key) {
  try {
    const raw = storage.getItem(key);
    return raw ? JSON.parse(raw) : null;
  } catch (error) {
    return null;
  }
}

function writeJson(storage, key, value) {
  try {
    storage.setItem(key, JSON.stringify(value));
  } catch (error) {
    // 無痕模式或儲存空間已滿時忽略
  }
}

/**
 * 取得暫存的收件資料
 * @returns {Object} { name, phone, email, address, shippingMethod }
 */
export function getCheckoutDraft() {
  return readJson(localStorage, CHECKOUT_DRAFT_KEY) || {};
}

/**
 * 暫存收件資料，下次結帳自動帶入
 * @param {Object} draft
 */
export function saveCheckoutDraft(draft) {
  writeJson(localStorage, CHECKOUT_DRAFT_KEY, draft);
}

/**
 * 保存最近一筆訂單摘要，供訂單完成頁顯示
 * @param {Object} order
 */
export function saveLastOrder(order) {
  writeJson(sessionStorage, LAST_ORDER_KEY, order);
}

/**
 * 取得最近一筆訂單摘要
 * @returns {Object|null}
 */
export function getLastOrder() {
  return readJson(sessionStorage, LAST_ORDER_KEY);
}

/**
 * 導向綠界付款頁
 * 後端回傳的 paymentUrl 為「付款網址 + 查詢參數」，綠界要求以 POST 表單送出
 * @param {string} paymentUrl
 */
export function redirectToPayment(paymentUrl) {
  const url = new URL(paymentUrl);
  const form = document.createElement('form');
  form.method = 'POST';
  form.action = `${url.origin}${url.pathname}`;
  form.style.display = 'none';

  url.searchParams.forEach((value, key) => {
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = key;
    input.value = value;
    form.appendChild(input);
  });

  document.body.appendChild(form);
  form.submit();
}

/**
 * 訂單狀態顯示文字與顏色
 */
export const ORDER_STATUS_MAP = {
  PENDING_PAYMENT: { label: '待付款', color: 'orange' },
  PAID: { label: '已付款', color: 'positive' },
  PROCESSING: { label: '處理中', color: 'primary' },
  COMPLETED: { label: '已完成', color: 'positive' },
  CANCELLED: { label: '已取消', color: 'grey' },
  REFUNDED: { label: '已退款', color: 'grey' },
};

/**
 * 取貨方式顯示文字
 */
export const PICKUP_TYPE_MAP = {
  DELIVERY: '宅配到府',
  STORE_PICKUP: '門市自取',
  CROSS_STORE_PICKUP: '跨店取貨',
};
