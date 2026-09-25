package com.info.ecommerce.common;

import com.info.ecommerce.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 單機記憶體限流（滑動視窗）：用於聯絡表單、忘記密碼、訂單查詢等公開端點，避免灌水與暴力猜測。
 * 多台主機部署時各自計算；來源 IP 請用 request.getRemoteAddr()（由 server.forward-headers-strategy 處理代理標頭）。
 */
@Component
public class RateLimiter {

    private static final int MAX_KEYS = 20_000;

    private final Map<String, Deque<Instant>> hits = new ConcurrentHashMap<>();

    /**
     * 記錄一次請求；同一 bucket + key 在 window 內超過 max 次時丟出 BusinessException
     */
    public void check(String bucket, String key, int max, Duration window, String message) {
        assertAllowed(bucket, key, max, window, message);
        record(bucket, key);
    }

    /** 只檢查不計數（例如登入：只有失敗才計數） */
    public void assertAllowed(String bucket, String key, int max, Duration window, String message) {
        if (key == null || key.isBlank()) {
            return;
        }
        Deque<Instant> recent = hits.get(keyOf(bucket, key));
        if (recent == null) {
            return;
        }
        Instant cutoff = Instant.now().minus(window);
        synchronized (recent) {
            while (!recent.isEmpty() && recent.peekFirst().isBefore(cutoff)) {
                recent.pollFirst();
            }
            if (recent.size() >= max) {
                throw new BusinessException(message);
            }
        }
    }

    /** 記錄一次 */
    public void record(String bucket, String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        Instant now = Instant.now();
        Deque<Instant> recent = hits.computeIfAbsent(keyOf(bucket, key), k -> new ArrayDeque<>());
        synchronized (recent) {
            recent.addLast(now);
        }
        if (hits.size() > MAX_KEYS) {
            evictStale(now.minus(Duration.ofHours(1)));
        }
    }

    /** 清除計數（例如登入成功後） */
    public void reset(String bucket, String key) {
        if (key != null && !key.isBlank()) {
            hits.remove(keyOf(bucket, key));
        }
    }

    private static String keyOf(String bucket, String key) {
        return bucket + "|" + key.trim().toLowerCase(java.util.Locale.ROOT);
    }

    /** 只移除已經一段時間沒有請求的 key，不影響仍在計數中的來源 */
    private void evictStale(Instant cutoff) {
        hits.entrySet().removeIf(entry -> {
            Deque<Instant> recent = entry.getValue();
            synchronized (recent) {
                return recent.isEmpty() || recent.peekLast().isBefore(cutoff);
            }
        });
    }
}
