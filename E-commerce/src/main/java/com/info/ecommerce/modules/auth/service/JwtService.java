package com.info.ecommerce.modules.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT token generation and validation
 */
@Service
public class JwtService {

    @Value("${jwt.secret:}")
    private String secretKey;

    private boolean temporarySecret;

    /** 是否使用啟動時產生的暫時金鑰（未設定 JWT_SECRET） */
    public boolean isTemporarySecret() {
        return temporarySecret;
    }

    /**
     * 啟動時檢查 JWT 金鑰：未設定時產生暫時金鑰（重啟後既有 token 失效），金鑰過短則拒絕啟動
     */
    @jakarta.annotation.PostConstruct
    void validateSecret() {
        if (secretKey == null || secretKey.isBlank()) {
            byte[] random = new byte[32];
            new java.security.SecureRandom().nextBytes(random);
            secretKey = java.util.Base64.getEncoder().encodeToString(random);
            temporarySecret = true;
            org.slf4j.LoggerFactory.getLogger(JwtService.class).warn(
                    "JWT_SECRET is not set; using a temporary random key. Tokens will be invalid after restart. "
                    + "Set JWT_SECRET (base64, at least 32 bytes, e.g. `openssl rand -base64 32`).");
            return;
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (RuntimeException e) {
            throw new IllegalStateException("jwt.secret / JWT_SECRET must be base64 encoded", e);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret / JWT_SECRET must decode to at least 32 bytes (256 bits)");
        }
    }

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        if (userDetails instanceof com.info.ecommerce.modules.auth.entity.User user && user.getId() != null) {
            claims.put(USER_ID_CLAIM, user.getId());
        }
        claims.put(PASSWORD_VERSION_CLAIM, passwordVersion(userDetails.getPassword()));
        return buildToken(claims, userDetails, jwtExpiration);
    }

    /** 帳號 ID：帳號被刪除後有人以相同帳號名稱註冊時，舊 token 不可沿用 */
    private static final String USER_ID_CLAIM = "uid";
    /** 密碼版本：變更 / 重設密碼後，先前發出的登入 token 全部失效 */
    private static final String PASSWORD_VERSION_CLAIM = "pwv";

    private String passwordVersion(String passwordHash) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(Decoders.BASE64.decode(secretKey), "HmacSHA256"));
            byte[] digest = mac.doFinal((passwordHash == null ? "" : passwordHash).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(java.util.Arrays.copyOf(digest, 12));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final Claims claims = extractAllClaims(token);
        // 用途型 token（例如 Email 驗證連結）不可當作登入憑證
        if (claims.get(PURPOSE_CLAIM) != null || !userDetails.getUsername().equals(claims.getSubject())
                || claims.getExpiration() == null || claims.getExpiration().before(new Date())) {
            return false;
        }
        // 舊版 token 沒有以下宣告時略過檢查（到期後自然淘汰）
        Object userId = claims.get(USER_ID_CLAIM);
        if (userId != null && userDetails instanceof com.info.ecommerce.modules.auth.entity.User user
                && !(userId instanceof Number number && user.getId() != null && number.longValue() == user.getId())) {
            return false;
        }
        Object version = claims.get(PASSWORD_VERSION_CLAIM);
        return version == null || version.equals(passwordVersion(userDetails.getPassword()));
    }

    private static final String PURPOSE_CLAIM = "purpose";

    /**
     * 產生特定用途的短效 token（例如 Email 驗證），與登入 token 以 purpose 宣告區隔
     */
    public String generatePurposeToken(String purpose, String subject, Map<String, Object> claims, long ttlMillis) {
        Map<String, Object> allClaims = new HashMap<>(claims);
        allClaims.put(PURPOSE_CLAIM, purpose);
        return Jwts.builder()
                .claims(allClaims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .compact();
    }

    /**
     * 驗證用途型 token 的簽章、有效期與用途；不符合時回傳 empty
     */
    public java.util.Optional<Claims> parsePurposeToken(String token, String purpose) {
        try {
            Claims claims = extractAllClaims(token);
            if (!purpose.equals(claims.get(PURPOSE_CLAIM)) || claims.getExpiration() == null
                    || claims.getExpiration().before(new Date())) {
                return java.util.Optional.empty();
            }
            return java.util.Optional.of(claims);
        } catch (RuntimeException e) {
            return java.util.Optional.empty();
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
