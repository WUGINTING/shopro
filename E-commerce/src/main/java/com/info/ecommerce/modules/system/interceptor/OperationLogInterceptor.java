package com.info.ecommerce.modules.system.interceptor;

import com.info.ecommerce.modules.auth.entity.User;
import com.info.ecommerce.modules.auth.repository.UserRepository;
import com.info.ecommerce.modules.system.dto.OperationLogDTO;
import com.info.ecommerce.modules.system.service.OperationLogAsyncService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 操作日誌攔截器
 * 自動記錄所有 API 請求的操作日誌
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogInterceptor implements HandlerInterceptor {

    private final OperationLogAsyncService operationLogAsyncService;
    private final UserRepository userRepository;

    // 需要排除的路徑
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/system/operation-logs",  // 排除操作日誌查詢本身
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );

    // 敏感操作路徑（需要特別標記）
    private static final List<String> SENSITIVE_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/logout",
            "/api/users",
            "/api/system"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 檢查是否需要記錄
        String requestPath = request.getRequestURI();
        if (shouldExclude(requestPath)) {
            return true;
        }

        // 記錄開始時間
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            String requestPath = request.getRequestURI();
            if (shouldExclude(requestPath)) {
                return;
            }

            // 獲取執行時間
            Long startTime = (Long) request.getAttribute("startTime");
            long executionTime = startTime != null ? System.currentTimeMillis() - startTime : 0;

            // 構建操作日誌
            OperationLogDTO logDTO = buildLogDTO(request, response, ex, executionTime);

            // 異步保存日誌
            saveLogAsync(logDTO);
        } catch (Exception e) {
            log.error("記錄操作日誌失敗", e);
        }
    }

    /**
     * 構建操作日誌 DTO
     */
    private OperationLogDTO buildLogDTO(HttpServletRequest request, HttpServletResponse response, Exception ex, long executionTime) {
        // 獲取用戶信息
        Long userId = null;
        String username = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            // 如果 Principal 是 User 實體（實現了 UserDetails），直接獲取
            if (principal instanceof User) {
                User user = (User) principal;
                userId = user.getId();
                username = user.getUsername();
                log.debug("從 User 實體獲取用戶信息: userId={}, username={}", userId, username);
            } 
            // 如果是 UserDetails，嘗試從數據庫查詢
            else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                username = userDetails.getUsername();
                log.debug("從 UserDetails 獲取用戶名: {}", username);
                // 從數據庫查詢用戶ID
                try {
                    User user = userRepository.findByUsername(username).orElse(null);
                    if (user != null) {
                        userId = user.getId();
                        log.debug("從數據庫查詢到用戶ID: {}", userId);
                    } else {
                        log.warn("數據庫中未找到用戶: {}", username);
                    }
                } catch (Exception e) {
                    log.warn("獲取用戶ID失敗: {}", e.getMessage());
                }
            }
            // 如果是字符串（可能是用戶名），也嘗試查詢
            else if (principal instanceof String) {
                username = (String) principal;
                log.debug("Principal 是字符串類型: {}", username);
                try {
                    User user = userRepository.findByUsername(username).orElse(null);
                    if (user != null) {
                        userId = user.getId();
                        log.debug("從數據庫查詢到用戶ID: {}", userId);
                    }
                } catch (Exception e) {
                    log.warn("獲取用戶ID失敗: {}", e.getMessage());
                }
            } else {
                log.debug("Principal 類型未知: {}", principal != null ? principal.getClass().getName() : "null");
            }
        } else {
            log.debug("未找到認證信息或未認證");
        }

        // 獲取請求信息
        String requestMethod = request.getMethod();
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString != null ? requestUrl + "?" + queryString : requestUrl;

        // 獲取請求體（如果有的話）
        String requestBody = null;
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper) request;
            byte[] contentAsByteArray = wrappedRequest.getContentAsByteArray();
            if (contentAsByteArray.length > 0) {
                requestBody = new String(contentAsByteArray, StandardCharsets.UTF_8);
                // 限制請求體長度，避免過大
                if (requestBody.length() > 5000) {
                    requestBody = requestBody.substring(0, 5000) + "...(truncated)";
                }
            }
        }

        // 獲取響應信息
        Integer responseStatus = response.getStatus();
        String responseBody = null;
        if (response instanceof ContentCachingResponseWrapper) {
            ContentCachingResponseWrapper wrappedResponse = (ContentCachingResponseWrapper) response;
            byte[] contentAsByteArray = wrappedResponse.getContentAsByteArray();
            if (contentAsByteArray.length > 0) {
                responseBody = new String(contentAsByteArray, StandardCharsets.UTF_8);
                // 限制響應體長度
                if (responseBody.length() > 5000) {
                    responseBody = responseBody.substring(0, 5000) + "...(truncated)";
                }
            }
        }

        // 獲取IP地址
        String ipAddress = getClientIpAddress(request);

        // 獲取User-Agent
        String userAgent = request.getHeader("User-Agent");

        // 判斷操作類型和模組
        String operationType = determineOperationType(requestMethod, requestUrl);
        String module = determineModule(requestUrl);
        String description = buildDescription(requestMethod, requestUrl, operationType, module);

        // 判斷是否為敏感操作
        boolean sensitive = isSensitiveOperation(requestUrl, operationType);

        // 判斷是否成功
        boolean success = ex == null && responseStatus >= 200 && responseStatus < 300;
        String errorMessage = ex != null ? ex.getMessage() : null;

        return OperationLogDTO.builder()
                .userId(userId)
                .username(username)
                .operationType(operationType)
                .module(module)
                .description(description)
                .requestMethod(requestMethod)
                .requestUrl(fullUrl)
                .requestParams(queryString)
                .requestBody(requestBody)
                .responseStatus(responseStatus)
                .responseBody(responseBody)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .executionTime(executionTime)
                .success(success)
                .errorMessage(errorMessage)
                .sensitive(sensitive)
                .build();
    }

    /**
     * 判斷操作類型
     */
    private String determineOperationType(String method, String url) {
        if (url.contains("/login")) {
            return "LOGIN";
        }
        if (url.contains("/logout")) {
            return "LOGOUT";
        }
        return switch (method) {
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            case "GET" -> "READ";
            default -> "UNKNOWN";
        };
    }

    /**
     * 判斷模組
     */
    private String determineModule(String url) {
        if (url.startsWith("/api/auth")) {
            return "AUTH";
        } else if (url.startsWith("/api/products") || url.startsWith("/api/categories")) {
            return "PRODUCT";
        } else if (url.startsWith("/api/orders")) {
            return "ORDER";
        } else if (url.startsWith("/api/crm") || url.startsWith("/api/customers") || url.startsWith("/api/members")) {
            return "CRM";
        } else if (url.startsWith("/api/system")) {
            return "SYSTEM";
        } else if (url.startsWith("/api/users")) {
            return "SYSTEM";
        } else if (url.startsWith("/api/marketing") || url.startsWith("/api/promotions")) {
            return "MARKETING";
        } else if (url.startsWith("/api/payment")) {
            return "PAYMENT";
        }
        return "OTHER";
    }

    /**
     * 構建操作描述
     */
    private String buildDescription(String method, String url, String operationType, String module) {
        String action = switch (operationType) {
            case "LOGIN" -> "登入";
            case "LOGOUT" -> "登出";
            case "CREATE" -> "創建";
            case "UPDATE" -> "更新";
            case "DELETE" -> "刪除";
            case "READ" -> "查詢";
            default -> "操作";
        };

        String resource = switch (module) {
            case "AUTH" -> "認證";
            case "PRODUCT" -> "商品";
            case "ORDER" -> "訂單";
            case "CRM" -> "客戶關係";
            case "SYSTEM" -> "系統";
            case "MARKETING" -> "營銷";
            case "PAYMENT" -> "支付";
            default -> "資源";
        };

        return action + resource;
    }

    /**
     * 判斷是否為敏感操作
     */
    private boolean isSensitiveOperation(String url, String operationType) {
        // 登入登出操作
        if ("LOGIN".equals(operationType) || "LOGOUT".equals(operationType)) {
            return true;
        }
        // 刪除操作
        if ("DELETE".equals(operationType)) {
            return true;
        }
        // 用戶管理操作
        if (url.contains("/users") && !"READ".equals(operationType)) {
            return true;
        }
        // 系統配置操作
        if (url.contains("/system") && !"READ".equals(operationType)) {
            return true;
        }
        // 檢查敏感路徑
        return SENSITIVE_PATHS.stream().anyMatch(url::contains);
    }

    /**
     * 獲取客戶端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 處理多個IP的情況
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 檢查是否應該排除該路徑
     */
    private boolean shouldExclude(String path) {
        return EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * 異步保存日誌
     */
    private void saveLogAsync(OperationLogDTO logDTO) {
        operationLogAsyncService.saveLogAsync(logDTO);
    }
}

