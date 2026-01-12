package com.info.ecommerce.modules.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付回調記錄實體
 * 記錄所有支付閘道的回調請求，用於除錯和追蹤
 */
@Entity
@Table(name = "payment_callback_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCallbackLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 支付閘道類型
     */
    @Column(name = "gateway", nullable = false, length = 50)
    private String gateway;

    /**
     * 訂單編號
     */
    @Column(name = "order_number", length = 100)
    private String orderNumber;

    /**
     * 交易 ID
     */
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    /**
     * 回調狀態（SUCCESS, FAILED, ERROR）
     */
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    /**
     * 原始請求參數（JSON 格式）
     */
    @Column(name = "raw_params", columnDefinition = "NVARCHAR(MAX)")
    private String rawParams;

    /**
     * 解析後的響應數據（JSON 格式）
     */
    @Column(name = "parsed_response", columnDefinition = "NVARCHAR(MAX)")
    private String parsedResponse;

    /**
     * 處理結果
     */
    @Column(name = "process_result", length = 500)
    private String processResult;

    /**
     * 錯誤訊息
     */
    @Column(name = "error_message", columnDefinition = "NVARCHAR(1000)")
    private String errorMessage;

    /**
     * 請求 IP
     */
    @Column(name = "request_ip", length = 50)
    private String requestIp;

    /**
     * User-Agent
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 處理時間（毫秒）
     */
    @Column(name = "process_time_ms")
    private Long processTimeMs;

    /**
     * 建立時間
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

