package com.info.ecommerce.modules.payment.service;

import com.info.ecommerce.modules.payment.entity.PaymentSetting;
import com.info.ecommerce.modules.payment.enums.PaymentGateway;
import com.info.ecommerce.modules.payment.repository.PaymentSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 支付設定初始化服務
 * 在應用啟動時自動初始化支付閘道設定
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSettingInitializer implements CommandLineRunner {

    private final PaymentSettingRepository settingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing payment settings...");
        
        // 檢查是否已有設定
        long count = settingRepository.count();
        if (count > 0) {
            log.info("Payment settings already initialized, count: {}", count);
            return;
        }
        
        // 初始化 LINE PAY 設定
        if (!settingRepository.findByGateway(PaymentGateway.LINE_PAY).isPresent()) {
            PaymentSetting linePay = PaymentSetting.builder()
                    .gateway(PaymentGateway.LINE_PAY)
                    .enabled(true)
                    .displayName("LINE PAY")
                    .description("LINE PAY 行動支付，支援 LINE 用戶快速付款")
                    .commissionRate(new BigDecimal("2.5"))
                    .maintenanceMode(false)
                    .sortOrder(1)
                    .build();
            settingRepository.save(linePay);
            log.info("Initialized LINE PAY settings");
        }
        
        // 初始化 ECPay 設定
        if (!settingRepository.findByGateway(PaymentGateway.ECPAY).isPresent()) {
            PaymentSetting ecPay = PaymentSetting.builder()
                    .gateway(PaymentGateway.ECPAY)
                    .enabled(true)
                    .displayName("綠界支付")
                    .description("ECPay 多元支付，支援信用卡、ATM、超商代碼、超商條碼等多種付款方式")
                    .commissionRate(new BigDecimal("2.8"))
                    .maintenanceMode(false)
                    .sortOrder(2)
                    .build();
            settingRepository.save(ecPay);
            log.info("Initialized ECPay settings");
        }
        
        // 初始化手動付款設定
        if (!settingRepository.findByGateway(PaymentGateway.MANUAL).isPresent()) {
            PaymentSetting manual = PaymentSetting.builder()
                    .gateway(PaymentGateway.MANUAL)
                    .enabled(true)
                    .displayName("手動付款")
                    .description("現金、銀行轉帳等手動記錄的付款方式")
                    .commissionRate(BigDecimal.ZERO)
                    .maintenanceMode(false)
                    .sortOrder(3)
                    .build();
            settingRepository.save(manual);
            log.info("Initialized Manual payment settings");
        }
        
        log.info("Payment settings initialization completed");
    }
}
