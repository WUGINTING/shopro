// HTML 清理共用方法（DOMPurify 白名單）
// 用於渲染後台撰寫的 HTML（部落格文章、自訂頁面）：只保留排版用的標籤與屬性，
// 移除 script、事件屬性、javascript: 連結、表單、iframe、SVG 動畫等可執行或釣魚用的內容。
import DOMPurify from 'dompurify';

const FORBID_TAGS = ['style', 'form', 'input', 'button', 'textarea', 'select', 'option', 'iframe', 'frame', 'frameset', 'object', 'embed', 'base', 'meta', 'link', 'svg', 'math'];
const FORBID_ATTR = ['style', 'formaction', 'action', 'srcdoc'];

let hooked = false;
function ensureHooks() {
  if (hooked) return;
  hooked = true;
  // 外部連結另開新視窗時避免 window.opener 被利用
  DOMPurify.addHook('afterSanitizeAttributes', node => {
    if (node.tagName === 'A' && node.getAttribute('target') === '_blank') {
      node.setAttribute('rel', 'noopener noreferrer');
    }
  });
}

/**
 * 跳脫 HTML 特殊字元（純文字顯示用）
 * @param {string} text
 * @returns {string}
 */
export function escapeHtml(text) {
  return String(text ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/**
 * 清理 HTML 字串，回傳可安全用於 v-html 的內容
 * @param {string} html - 原始 HTML
 * @returns {string} 清理後的 HTML
 */
export function sanitizeHtml(html) {
  if (!html || typeof html !== 'string') return '';
  // 沒有 DOM（伺服器端）時不輸出 HTML，只顯示純文字
  if (typeof window === 'undefined' || !DOMPurify.isSupported) {
    return escapeHtml(html);
  }
  ensureHooks();
  return DOMPurify.sanitize(html, {
    USE_PROFILES: { html: true },
    FORBID_TAGS,
    FORBID_ATTR,
    ALLOW_DATA_ATTR: false,
  });
}
