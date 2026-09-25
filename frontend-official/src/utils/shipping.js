import { getShippingOptions } from 'src/api/order.js';

/**
 * 運費說明文字（依後台運費設定），供首頁與常見問題使用；載入一次後快取
 */
let cached = null;

export async function loadShippingOptions() {
  if (!cached) {
    cached = getShippingOptions()
      .then(res => (Array.isArray(res?.data) ? res.data : []))
      .catch(() => {
        cached = null;
        return [];
      });
  }
  return cached;
}

const money = value => `NT$${Number(value || 0).toLocaleString()}`;

export function describeOption(option) {
  const fee = Number(option.fee || 0);
  if (fee <= 0) return `${option.name}：免運費`;
  if (option.freeShippingThreshold) {
    return `${option.name}：運費 ${money(fee)}，單筆訂單商品金額滿 ${money(option.freeShippingThreshold)} 免運費`;
  }
  return `${option.name}：運費 ${money(fee)}`;
}

/** 首頁標語，例如「滿 NT$1,000 免運」或「門市自取免運」 */
export function shippingHeadline(options) {
  const delivery = options.find(option => option.method === 'HOME_DELIVERY');
  if (delivery && Number(delivery.fee || 0) <= 0) return '宅配免運';
  if (delivery?.freeShippingThreshold) return `滿 ${money(delivery.freeShippingThreshold)} 免運`;
  if (options.some(option => Number(option.fee || 0) <= 0)) return '門市自取免運';
  return '快速出貨';
}

export function shippingSubline(options) {
  return options.map(option => option.name).join('或') || '宅配到府或門市自取';
}
