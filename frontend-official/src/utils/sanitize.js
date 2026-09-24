// HTML 清理共用方法
// 用於渲染後台管理員撰寫的 HTML（部落格文章、自訂頁面）前的基本防護：
// 移除 <script> 等可執行標籤、on* 事件屬性，以及 javascript: 開頭的連結。

// 直接移除（連同內容）的標籤
const BLOCKED_TAGS = ['script', 'noscript', 'object', 'embed', 'base', 'meta', 'link'];

// 可能帶有網址的屬性
const URL_ATTRS = ['href', 'src', 'xlink:href', 'action', 'formaction', 'srcset'];

// 危險的網址協定（忽略空白與控制字元後比對）
const UNSAFE_URL = /^(javascript|vbscript|data:text\/html)/i;

/**
 * 以正規表示式清理（無 DOM 環境時的備援）
 * @param {string} html
 * @returns {string}
 */
function sanitizeWithRegex(html) {
  return html
    .replace(/<script[\s\S]*?>[\s\S]*?<\/script\s*>/gi, '')
    .replace(/<\/?script[^>]*>/gi, '')
    .replace(/\s+on[a-z-]+\s*=\s*("[^"]*"|'[^']*'|[^\s>]+)/gi, '')
    .replace(
      /\s(href|src)\s*=\s*("\s*javascript:[^"]*"|'\s*javascript:[^']*'|javascript:[^\s>]+)/gi,
      ''
    );
}

/**
 * 清理 HTML 字串，回傳可安全用於 v-html 的內容
 * @param {string} html - 原始 HTML
 * @returns {string} 清理後的 HTML
 */
export function sanitizeHtml(html) {
  if (!html || typeof html !== 'string') return '';

  if (typeof document === 'undefined') {
    return sanitizeWithRegex(html);
  }

  // <template> 解析內容時不會執行腳本或載入資源
  const template = document.createElement('template');
  template.innerHTML = html;

  template.content
    .querySelectorAll(BLOCKED_TAGS.join(','))
    .forEach(node => node.remove());

  template.content.querySelectorAll('*').forEach(el => {
    Array.from(el.attributes).forEach(attr => {
      const name = attr.name.toLowerCase();
      if (name.startsWith('on') || name === 'srcdoc') {
        el.removeAttribute(attr.name);
        return;
      }
      if (URL_ATTRS.includes(name)) {
        // eslint-disable-next-line no-control-regex
        const value = attr.value.replace(/[\s\u0000-\u001f]/g, '');
        if (UNSAFE_URL.test(value)) {
          el.removeAttribute(attr.name);
        }
      }
    });

    // 外部連結另開新視窗時避免 window.opener 被利用
    if (el.tagName === 'A' && el.getAttribute('target') === '_blank') {
      el.setAttribute('rel', 'noopener noreferrer');
    }
  });

  return template.innerHTML;
}
