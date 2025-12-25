package com.info.ecommerce.modules.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LINE PAY 支付閘道配置
 */
@Configuration
@ConfigurationProperties(prefix = "payment.linepay")
@Data
public class LinePayConfig {
    
    /**
     * LINE PAY Channel ID
     */
    private String channelId;
    
    /**
     * LINE PAY Channel Secret Key
     */
    private String channelSecretKey;
    
    /**
     * LINE PAY API Base URL
     */
    private String apiUrl = "https://sandbox-api-pay.line.me";
    
    /**
     * 付款確認 Callback URL
     */
    private String confirmUrl;
    
    /**
     * 付款取消 URL
     */
    private String cancelUrl;
    
    /**
     * 是否為測試環境
     */
    private boolean sandbox = true;
}
