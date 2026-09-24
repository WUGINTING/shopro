import http from '@/utils/request';

/**
 * 自訂頁面（CMS）相關 API
 */

/**
 * 依別名取得已啟用的自訂頁面
 * 頁面不存在或未啟用時，後端回傳 400「自訂頁面不存在」
 * @param {string} slug - 頁面別名
 * @param {Object} config - axios 設定（例如 { silent: true }）
 * @returns {Promise} CustomPageDTO
 */
export function getCustomPageBySlug(slug, config = {}) {
  return http.get(
    `/crm/custom-pages/slug/${encodeURIComponent(slug)}`,
    undefined,
    config
  );
}
