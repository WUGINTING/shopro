// 格式化共用方法

/**
 * 格式化數字為貨幣格式
 * @param {Number} amount - 金額
 * @param {String} currency - 貨幣符號
 * @returns {String} 格式化後的金額
 */
export function formatCurrency(amount, currency = 'NT$') {
  return `${currency} ${amount.toLocaleString()}`;
}

/**
 * 格式化日期
 * @param {String|Date} date - 日期
 * @param {String} format - 格式 (default: 'YYYY-MM-DD')
 * @returns {String} 格式化後的日期
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 截斷文字
 * @param {String} text - 文字
 * @param {Number} length - 最大長度
 * @param {String} suffix - 後綴
 * @returns {String} 截斷後的文字
 */
export function truncateText(text, length = 50, suffix = '...') {
  if (!text || text.length <= length) return text;
  return text.substring(0, length) + suffix;
}
