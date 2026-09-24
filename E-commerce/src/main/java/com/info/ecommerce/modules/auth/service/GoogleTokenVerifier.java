package com.info.ecommerce.modules.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

/**
 * 驗證 Google ID Token：由 Google tokeninfo 端點驗證簽章與有效期，
 * 並檢查 aud 為本站的 Client ID、iss 為 Google、Email 已驗證。
 * 未設定 GOOGLE_CLIENT_ID 時停用 Google 登入。
 */
@Slf4j
@Component
public class GoogleTokenVerifier {

    private static final String TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.google.client-id:}")
    private String clientId;

    @Data
    @Builder
    public static class GoogleUser {
        private String email;
        private String name;
        private String picture;
    }

    public boolean isEnabled() {
        return clientId != null && !clientId.isBlank();
    }

    /**
     * @return 驗證成功時回傳使用者資料；token 無效時回傳 empty
     */
    public Optional<GoogleUser> verify(String idToken) {
        if (!isEnabled() || idToken == null || idToken.isBlank()) {
            return Optional.empty();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(
                            URI.create(TOKENINFO_URL + URLEncoder.encode(idToken, StandardCharsets.UTF_8)))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.info("Google tokeninfo rejected token: HTTP {}", response.statusCode());
                return Optional.empty();
            }
            return parseClaims(objectMapper.readTree(response.body()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Failed to verify Google ID token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    Optional<GoogleUser> parseClaims(JsonNode claims) {
        String aud = claims.path("aud").asText("");
        String iss = claims.path("iss").asText("");
        String email = claims.path("email").asText("");
        boolean emailVerified = "true".equalsIgnoreCase(claims.path("email_verified").asText(""));

        if (!clientId.equals(aud)) {
            log.warn("Google token audience mismatch");
            return Optional.empty();
        }
        if (!"accounts.google.com".equals(iss) && !"https://accounts.google.com".equals(iss)) {
            log.warn("Google token issuer mismatch: {}", iss);
            return Optional.empty();
        }
        if (email.isBlank() || !emailVerified) {
            return Optional.empty();
        }
        String name = claims.path("name").asText("");
        return Optional.of(GoogleUser.builder()
                .email(email)
                .name(name.isBlank() ? email.split("@")[0] : name)
                .picture(claims.path("picture").asText(null))
                .build());
    }
}
