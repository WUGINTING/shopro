package com.info.ecommerce.modules.payment.config;

import com.info.ecommerce.modules.payment.entity.EcPayConfigEntity;
import com.info.ecommerce.modules.payment.repository.EcPayConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Optional;

/**
 * ECPay 綠界支付閘道配置
 * 從資料庫讀取配置，如果資料庫沒有配置則使用 application.properties 的預設值
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "payment.ecpay")
@Data
@DependsOn("ecPayConfigRepository")
public class EcPayConfig {
    
    @Autowired(required = false)
    private EcPayConfigRepository ecPayConfigRepository;
    
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
    
    /**
     * 初始化時從資料庫載入配置
     */
    @PostConstruct
    public void loadFromDatabase() {
        if (ecPayConfigRepository != null) {
            try {
                Optional<EcPayConfigEntity> configOpt = ecPayConfigRepository.findByEnabledTrue()
                        .or(() -> ecPayConfigRepository.findFirstByOrderByIdAsc());
                
                if (configOpt.isPresent()) {
                    EcPayConfigEntity entity = configOpt.get();
                    this.merchantId = entity.getMerchantId();
                    this.hashKey = entity.getHashKey();
                    this.hashIV = entity.getHashIV();
                    this.apiUrl = entity.getApiUrl();
                    this.returnUrl = entity.getReturnUrl();
                    this.notifyUrl = entity.getNotifyUrl();
                    this.sandbox = entity.getSandbox() != null ? entity.getSandbox() : true;
                    log.info("ECPay 配置已從資料庫載入: MerchantID={}, Sandbox={}", 
                            this.merchantId, this.sandbox);
                } else {
                    log.warn("資料庫中沒有 ECPay 配置，使用 application.properties 的預設值");
                }
            } catch (Exception e) {
                log.error("從資料庫載入 ECPay 配置失敗，使用 application.properties 的預設值", e);
            }
        }
    }
    
    /**
     * 重新載入配置（當配置更新時調用）
     */
    public void reload() {
        loadFromDatabase();
    }
}
