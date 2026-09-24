package com.info.ecommerce.modules.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Spring Security 設定（JWT，無狀態）
 *
 * <p>授權分四層（由上而下比對）：</p>
 * <ol>
 *   <li>公開：前台商城需要的唯讀 API、訪客結帳、登入註冊、金流回呼</li>
 *   <li>已登入即可：會員自己的資料與訂單（控制器內另有擁有權檢查）</li>
 *   <li>僅 ADMIN：使用者、系統與金流設定等含機敏資料的 API</li>
 *   <li>其餘全部：後台員工（ADMIN / MANAGER / STAFF）</li>
 * </ol>
 * 控制器上的 {@code @PreAuthorize} 仍會在此之上再做一次檢查。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    static final String[] STAFF_ROLES = {"ADMIN", "MANAGER", "STAFF"};

    // 注意：RegexRequestMatcher 比對的字串包含查詢參數，因此所有 regex 結尾都允許選用的 ?query

    /** 前台公開唯讀 API（GET） */
    static final String[] PUBLIC_GET = {
            "/api/products",
            "/api/products/search",
            "/api/products/status/**",
            "/api/products/category/**",
            "/api/products/*/description-blocks/**",
            "/api/products/*/description-blocks",
            "/api/product-categories/**",
            "/api/product-specifications/**",
            "/api/product-images/product/**",
            "/api/product-tags",
            "/api/product-tags/product/**",
            "/api/albums/images/**",
            "/api/crm/blog/slug/**",
            "/api/crm/blog/status/PUBLISHED",
            "/api/crm/custom-pages/slug/**",
            "/api/crm/custom-pages/enabled",
            "/api/homepage-blocks/enabled",
            "/api/popup-ads/active",
            "/api/system/config/store-content",
            "/api/store",
            "/api/marketing/promotions",
            "/api/marketing/promotions/*",
            "/api/payment-gateway/callback/linepay/**",
    };

    static final String[] API_DOCS = {"/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"};

    /** 含金鑰、帳號或全站設定的 API，只允許 ADMIN */
    static final String[] ADMIN_ONLY = {
            "/api/users/**",
            "/api/system/**",
            "/api/payment/ecpay/config/**",
            "/api/payment/callback-logs/**",
            "/api/payment-management/settings/**",
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF is disabled because this is a stateless REST API using JWT bearer tokens.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(API_DOCS).permitAll()

                        // 1. 公開
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register", "/api/auth/google",
                                "/api/auth/email-verification/confirm").permitAll()
                        .requestMatchers("/api/storefront/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payment-gateway/callback/ecpay").permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "^/api/products/\\d+(\\?.*)?$")).permitAll()

                        // 2. 已登入（會員）：控制器內檢查只能存取自己的資料
                        .requestMatchers("/api/auth/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/email-verification").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/orders/my").authenticated()
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "^/api/orders/\\d+(\\?.*)?$")).authenticated()
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "^/api/orders/customer/\\d+(\\?.*)?$")).authenticated()
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "^/api/orders/qa/order/\\d+(\\?.*)?$")).authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/orders/qa").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/payment-gateway/create", "/api/payment-gateway/confirm").authenticated()

                        // 3. 僅 ADMIN
                        .requestMatchers(ADMIN_ONLY).hasRole("ADMIN")

                        // 4. 其餘皆為後台
                        .anyRequest().hasAnyRole(STAFF_ROLES)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                writeJson(response, HttpStatus.UNAUTHORIZED, "請先登入"))
                        .accessDeniedHandler((request, response, e) ->
                                writeJson(response, HttpStatus.FORBIDDEN, "權限不足"))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void writeJson(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\",\"data\":null}");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
