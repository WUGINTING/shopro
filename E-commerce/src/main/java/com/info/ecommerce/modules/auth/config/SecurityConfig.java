package com.info.ecommerce.modules.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

/**
 * Security configuration for Spring Security with JWT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF is disabled because this is a stateless REST API using JWT tokens.
                // JWT tokens are immune to CSRF attacks as they are not automatically sent by browsers.
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 公開頁面 - 不需要認證
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api/albums/images/**",  // 允許公開訪問相冊圖片
                                "/api/products",  // 商品列表（公開）
                                "/api/products/**",  // 商品詳情、分類、狀態、搜尋（公開）
                                "/api/product-categories",  // 商品分類列表（公開）
                                "/api/product-categories/**",  // 商品分類詳情、啟用、頂層、子分類（公開）
                                "/api/product-specifications/**"  // 商品規格查詢（公開，POST/PUT/DELETE 方法由 @PreAuthorize 保護）
                        ).permitAll()
                        // 管理員專屬 API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 經理級以上 API (ADMIN, MANAGER)
                        .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        // 員工級以上 API (ADMIN, MANAGER, STAFF)
                        .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        // 客戶專屬 API
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                        // 其他需要認證的請求
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
