package com.info.ecommerce.common;

import com.info.ecommerce.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 單機記憶體限流（滑動視窗）：用於聯絡表單、忘記密碼、登入、訂單查詢等公開端點，避免灌水與暴力猜測。
 * <ul>
 *   <li>檢查與計數在同一個原子操作內完成，同時送出的大量請求也不會超過上限。</li>
 *   <li>「只算失敗」的情境先 {@link #check} 佔用名額，成功後再 {@link #release} 歸還。</li>
 *   <li>記憶體有上限：每分鐘最多清理一次閒置的 key；超過硬上限時依序移除較不重要的計數，
 *       帳號層級的登入失敗次數一律保留（不會因為被灌爆而解除）。</li>
 * </ul>
 * 多台主機部署時各自計算；來源 IP 請用 {@link #clientKey}（由 server.forward-headers-strategy 處理代理標頭）。
 */
@Component
public class RateLimiter {

    private static final int EVICT_THRESHOLD = 20_000;
    private static final int HARD_LIMIT = 200_000;
    private static final Duration IDLE_EXPIRY = Duration.ofHours(1);

    private final Map<String, Deque<Instant>> hits = new ConcurrentHashMap<>();
    private final AtomicLong lastEviction = new AtomicLong();

    /**
     * 記錄一次請求；同一 bucket + key 在 window 內已達 max 次時丟出 BusinessException（不計入）
     */
    public void check(String bucket, String key, int max, Duration window, String message) {
        if (key == null || key.isBlank()) {
            return;
        }
        Instant now = Instant.now();
        Instant cutoff = now.minus(window);
        // compute 對同一個 key 是原子操作；丟出例外時對應的值不變
        hits.compute(keyOf(bucket, key), (k, recent) -> {
            Deque<Instant> deque = recent != null ? recent : new ArrayDeque<>();
            while (!deque.isEmpty() && deque.peekFirst().isBefore(cutoff)) {
                deque.pollFirst();
            }
            if (deque.size() >= max) {
                throw new BusinessException(message);
            }
            deque.addLast(now);
            return deque;
        });
        maybeEvict(now);
    }

    /** 歸還最近一次佔用的名額（例如登入成功、查詢成功時不計入失敗次數） */
    public void release(String bucket, String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        hits.computeIfPresent(keyOf(bucket, key), (k, recent) -> {
            recent.pollLast();
            return recent.isEmpty() ? null : recent;
        });
    }

    /** 清除計數（例如密碼重設成功後解除登入暫停） */
    public void reset(String bucket, String key) {
        if (key != null && !key.isBlank()) {
            hits.remove(keyOf(bucket, key));
        }
    }

    /** 清除某個 bucket 內以 keyPrefix 開頭的所有計數（例如某帳號在各來源的登入失敗次數） */
    public void resetPrefix(String bucket, String keyPrefix) {
        if (keyPrefix != null && !keyPrefix.isBlank()) {
            String prefix = keyOf(bucket, keyPrefix);
            hits.keySet().removeIf(key -> key.startsWith(prefix));
        }
    }

    /**
     * 限流用的來源識別：IPv4 用完整位址；IPv6 取前 64 位元（同一用戶通常擁有整個 /64，可任意更換後段位址）
     */
    public static String clientKey(HttpServletRequest request) {
        String address = request.getRemoteAddr();
        if (address == null || !address.contains(":")) {
            return address;
        }
        try {
            byte[] bytes = java.net.InetAddress.getByName(address).getAddress();
            if (bytes.length == 16) {
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < 8; i += 2) {
                    prefix.append(String.format("%02x%02x:", bytes[i], bytes[i + 1]));
                }
                return prefix.append(":/64").toString();
            }
        } catch (java.net.UnknownHostException | RuntimeException ignored) {
            // 無法解析時使用原字串
        }
        return address;
    }

    private void evictIdleSince(Instant cutoff, boolean keepProtected) {
        for (String key : hits.keySet()) {
            if (keepProtected && key.startsWith(PROTECTED_PREFIX)) {
                continue;
            }
            hits.computeIfPresent(key, (k, recent) ->
                    recent.isEmpty() || recent.peekLast().isBefore(cutoff) ? null : recent);
        }
    }

    /** 帳號層級的登入失敗計數，記憶體不足時也不清除 */
    public static final String PROTECTED_BUCKET = "login-user";
    private static final String PROTECTED_PREFIX = PROTECTED_BUCKET + "|";

    private static String keyOf(String bucket, String key) {
        return bucket + "|" + key.trim().toLowerCase(Locale.ROOT);
    }

    private void maybeEvict(Instant now) {
        if (hits.size() <= EVICT_THRESHOLD) {
            return;
        }
        long last = lastEviction.get();
        if (now.toEpochMilli() - last < 60_000 || !lastEviction.compareAndSet(last, now.toEpochMilli())) {
            return;
        }
        evictIdleSince(now.minus(IDLE_EXPIRY), false);
        if (hits.size() > HARD_LIMIT) {
            // 仍超過上限（大量不同來源灌入）：先移除 15 分鐘內沒有新請求的計數，
            // 再不夠才移除非帳號層級的計數；帳號層級的登入失敗次數（數量受限於實際帳號數）一律保留
            evictIdleSince(now.minus(Duration.ofMinutes(15)), false);
            if (hits.size() > HARD_LIMIT) {
                evictIdleSince(Instant.MAX, true);
            }
        }
    }
}
