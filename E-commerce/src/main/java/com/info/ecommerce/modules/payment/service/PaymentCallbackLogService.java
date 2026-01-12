package com.info.ecommerce.modules.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.payment.dto.PaymentCallbackLogDTO;
import com.info.ecommerce.modules.payment.entity.PaymentCallbackLog;
import com.info.ecommerce.modules.payment.repository.PaymentCallbackLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付回調記錄服務
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCallbackLogService {

    private final PaymentCallbackLogRepository callbackLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * 記錄回調請求
     */
    @Transactional
    public PaymentCallbackLog logCallback(
            String gateway,
            Map<String, String> rawParams,
            Object parsedResponse,
            String status,
            String processResult,
            String errorMessage,
            long processTimeMs) {
        
        try {
            PaymentCallbackLog callbackLog = PaymentCallbackLog.builder()
                    .gateway(gateway)
                    .status(status)
                    .rawParams(convertToJson(rawParams))
                    .parsedResponse(convertToJson(parsedResponse))
                    .processResult(processResult)
                    .errorMessage(errorMessage)
                    .processTimeMs(processTimeMs)
                    .requestIp(getRequestIp())
                    .userAgent(getUserAgent())
                    .createdAt(LocalDateTime.now())
                    .build();
            
            // 從解析後的響應中提取訂單編號和交易 ID
            if (parsedResponse != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseMap = objectMapper.convertValue(parsedResponse, Map.class);
                    if (responseMap.containsKey("orderNumber")) {
                        callbackLog.setOrderNumber(String.valueOf(responseMap.get("orderNumber")));
                    }
                    if (responseMap.containsKey("transactionId")) {
                        callbackLog.setTransactionId(String.valueOf(responseMap.get("transactionId")));
                    }
                } catch (Exception e) {
                    log.warn("Failed to extract orderNumber/transactionId from parsed response", e);
                }
            }
            
            PaymentCallbackLog saved = callbackLogRepository.save(callbackLog);
            log.info("Payment callback logged: ID={}, Gateway={}, OrderNumber={}, Status={}", 
                    saved.getId(), gateway, saved.getOrderNumber(), status);
            
            return saved;
        } catch (Exception e) {
            log.error("Failed to log payment callback", e);
            return null;
        }
    }

    /**
     * 取得回調記錄（分頁）
     */
    @Transactional(readOnly = true)
    public Page<PaymentCallbackLogDTO> getCallbackLogs(Pageable pageable) {
        return callbackLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toDTO);
    }

    /**
     * 根據訂單編號查詢回調記錄
     */
    @Transactional(readOnly = true)
    public List<PaymentCallbackLogDTO> getCallbackLogsByOrderNumber(String orderNumber) {
        return callbackLogRepository.findByOrderNumberOrderByCreatedAtDesc(orderNumber)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根據交易 ID 查詢回調記錄
     */
    @Transactional(readOnly = true)
    public List<PaymentCallbackLogDTO> getCallbackLogsByTransactionId(String transactionId) {
        return callbackLogRepository.findByTransactionIdOrderByCreatedAtDesc(transactionId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根據支付閘道類型查詢回調記錄
     */
    @Transactional(readOnly = true)
    public Page<PaymentCallbackLogDTO> getCallbackLogsByGateway(String gateway, Pageable pageable) {
        return callbackLogRepository.findByGatewayOrderByCreatedAtDesc(gateway, pageable)
                .map(this::toDTO);
    }

    /**
     * 取得回調記錄詳情
     */
    @Transactional(readOnly = true)
    public PaymentCallbackLogDTO getCallbackLog(Long id) {
        PaymentCallbackLog log = callbackLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("回調記錄不存在"));
        return toDTO(log);
    }

    /**
     * 轉換為 JSON 字符串
     */
    private String convertToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert object to JSON", e);
            return obj.toString();
        }
    }

    /**
     * 取得請求 IP
     */
    private String getRequestIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.warn("Failed to get request IP", e);
        }
        return null;
    }

    /**
     * 取得 User-Agent
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.warn("Failed to get User-Agent", e);
        }
        return null;
    }

    /**
     * 轉換 Entity 到 DTO
     */
    private PaymentCallbackLogDTO toDTO(PaymentCallbackLog log) {
        PaymentCallbackLogDTO dto = new PaymentCallbackLogDTO();
        BeanUtils.copyProperties(log, dto);
        return dto;
    }
}

