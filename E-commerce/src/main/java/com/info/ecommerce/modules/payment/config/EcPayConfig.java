package com.info.ecommerce.modules.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ECPay 綠界支付閘道配置
 */
@Configuration
@ConfigurationProperties(prefix = "payment.ecpay")
@Data
public class EcPayConfig {
    
    /**
     * ECPay 商店代號 (MerchantID)
     */
    private String merchantId;
    
    /**
     * ECPay HashKey
     */
    private String hashKey;
    
    /**
     * ECPay HashIV
     */
    private String hashIV;
    
    /**
     * ECPay API Base URL
     */
    private String apiUrl = "https://payment-stage.ecpay.com.tw";
    
    /**
     * 付款完成返回 URL
     */
    private String returnUrl;
    
    /**
     * 付款結果通知 URL (OrderResultURL)
     */
    private String notifyUrl;
    
    /**
     * 是否為測試環境
     */
    private boolean sandbox = true;
}
