import { useMeta } from 'quasar';

const SITE_NAME = '遇日小舖';

/**
 * 前台頁面標題與描述（SEO / 分享預覽）
 * @param {() => {title?: string, description?: string, image?: string}} getter - 回傳目前頁面資訊的函式（可依資料變動）
 */
export function useShopMeta(getter) {
  useMeta(() => {
    const { title, description, image } = getter() || {};
    const fullTitle = title ? `${title} | ${SITE_NAME}` : SITE_NAME;
    const meta = {
      ogTitle: { property: 'og:title', content: fullTitle },
      ogType: { property: 'og:type', content: 'website' },
    };
    if (description) {
      const text = String(description).replace(/<[^>]*>/g, '').replace(/\s+/g, ' ').trim().slice(0, 160);
      meta.description = { name: 'description', content: text };
      meta.ogDescription = { property: 'og:description', content: text };
    }
    if (image) {
      meta.ogImage = { property: 'og:image', content: image };
    }
    return { title: fullTitle, meta };
  });
}
