// HTML 清理（DOMPurify 白名單）：渲染後台撰寫的 HTML（部落格文章等）前使用，
// 移除 script、事件屬性、javascript: 連結、表單、iframe、SVG 動畫等可執行或釣魚用的內容。
// 後台 App 與顧客商城同網域且登入 token 存在 localStorage，務必在 v-html 前清理。
import DOMPurify from 'dompurify'

const FORBID_TAGS = ['style', 'form', 'input', 'button', 'textarea', 'select', 'option', 'iframe', 'frame', 'frameset', 'object', 'embed', 'base', 'meta', 'link', 'svg', 'math']
const FORBID_ATTR = ['style', 'formaction', 'action', 'srcdoc']

let hooked = false

export const sanitizeHtml = (html: string | null | undefined): string => {
  if (!html) return ''
  if (!hooked) {
    hooked = true
    DOMPurify.addHook('afterSanitizeAttributes', (node) => {
      if (node.tagName === 'A' && node.getAttribute('target') === '_blank') {
        node.setAttribute('rel', 'noopener noreferrer')
      }
    })
  }
  return DOMPurify.sanitize(html, {
    USE_PROFILES: { html: true },
    FORBID_TAGS,
    FORBID_ATTR,
    ALLOW_DATA_ATTR: false
  })
}
