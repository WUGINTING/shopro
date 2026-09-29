import http from '@/utils/request';

/**
 * 部落格（最新消息）相關 API
 * 前台僅能讀取已發布（PUBLISHED）的文章
 */

/**
 * 分頁查詢已發布文章
 * @param {Object} params - 查詢參數
 * @param {number} params.page - 頁碼（從 0 開始）
 * @param {number} params.size - 每頁數量
 * @param {Object} config - axios 設定（例如 { silent: true }）
 * @returns {Promise} Page<BlogPostDTO>（content, totalElements, totalPages...）
 */
export function getPublishedPosts(params = {}, config = {}) {
  return http.get(
    '/crm/blog/status/PUBLISHED',
    {
      page: params.page || 0,
      size: params.size || 9,
    },
    config
  );
}

/**
 * 依別名取得已發布文章（後端會同時累加瀏覽次數）
 * @param {string} slug - 文章別名
 * @param {Object} config - axios 設定
 * @returns {Promise} BlogPostDTO
 */
export function getPostBySlug(slug, config = {}) {
  return http.get(`/crm/blog/slug/${encodeURIComponent(slug)}`, undefined, config);
}

// 文章索引快取（供上一篇 / 下一篇 / 相關文章使用）
const INDEX_SIZE = 100;
let publishedIndexPromise = null;

/**
 * 取得最近的已發布文章索引（最多 100 篇，依發布時間新到舊排序）
 * 結果於本次瀏覽期間快取；失敗時清除快取
 * @returns {Promise<Array>} BlogPostDTO[]
 */
export function getPublishedIndex() {
  if (!publishedIndexPromise) {
    publishedIndexPromise = getPublishedPosts(
      { page: 0, size: INDEX_SIZE },
      { silent: true }
    )
      .then(res => sortByPublishedDesc(res?.data?.content || []))
      .catch(error => {
        publishedIndexPromise = null;
        throw error;
      });
  }
  return publishedIndexPromise;
}

/**
 * 依發布時間由新到舊排序
 * @param {Array} posts - BlogPostDTO[]
 * @returns {Array}
 */
export function sortByPublishedDesc(posts = []) {
  const time = post => {
    const t = new Date(post?.publishedAt || 0).getTime();
    return Number.isNaN(t) ? 0 : t;
  };
  return [...posts].sort((a, b) => time(b) - time(a) || (b.id ?? 0) - (a.id ?? 0));
}

/**
 * 解析文章標籤（後端以逗號分隔，也相容全形逗號與頓號）
 * @param {string} tags - 標籤字串
 * @returns {string[]}
 */
export function parseTags(tags) {
  if (!tags || typeof tags !== 'string') return [];
  const seen = new Set();
  return tags
    .split(/[,，、]/)
    .map(tag => tag.trim())
    .filter(tag => tag && !seen.has(tag) && seen.add(tag));
}

/**
 * 從 HTML 內容擷取純文字摘要
 * @param {string} html - 文章內容
 * @param {number} length - 最大長度
 * @returns {string}
 */
export function htmlToExcerpt(html, length = 80) {
  if (!html) return '';
  const text = html
    .replace(/<(script|style)[\s\S]*?<\/\1>/gi, ' ')
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/g, ' ')
    .replace(/&amp;/g, '&')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/\s+/g, ' ')
    .trim();
  return text.length > length ? `${text.slice(0, length)}…` : text;
}
