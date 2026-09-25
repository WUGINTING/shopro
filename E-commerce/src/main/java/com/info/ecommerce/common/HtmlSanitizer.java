package com.info.ecommerce.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * 後台撰寫的 HTML（部落格文章、自訂頁面）儲存前的白名單清理：
 * 只保留排版用的標籤與屬性，移除 script、事件屬性、javascript: 連結、表單、iframe 等。
 * 前台顯示時仍會再以 DOMPurify 清理一次（雙重防護）。
 */
public final class HtmlSanitizer {

    private static final Safelist SAFELIST = Safelist.relaxed()
            .addTags("hr", "figure", "figcaption", "mark", "del", "ins", "s")
            .addAttributes(":all", "class")
            .addAttributes("a", "target", "rel")
            .addProtocols("a", "href", "http", "https", "mailto", "tel")
            .addProtocols("img", "src", "http", "https")
            .preserveRelativeLinks(true);

    private HtmlSanitizer() {
    }

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        Document.OutputSettings output = new Document.OutputSettings().prettyPrint(false);
        // baseUri 讓相對路徑（例如 /uploads/...）的圖片與連結可以保留
        return Jsoup.clean(html, "https://localhost", SAFELIST, output);
    }
}
