package com.info.ecommerce.modules.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ecommerce.modules.system.entity.Settings;
import com.info.ecommerce.modules.system.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系統設置服務
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;
    private final ObjectMapper objectMapper;

    /**
     * 獲取系統設置
     */
    public Map<String, Object> getSystemSettings() {
        return getSettingsByType("SYSTEM");
    }

    /**
     * 更新系統設置
     */
    @Transactional
    public Map<String, Object> updateSystemSettings(Map<String, Object> settings) {
        return saveSettings("SYSTEM", settings);
    }

    /**
     * 獲取郵件設置
     */
    public Map<String, Object> getEmailSettings() {
        return getSettingsByType("EMAIL");
    }

    /**
     * 更新郵件設置
     */
    @Transactional
    public Map<String, Object> updateEmailSettings(Map<String, Object> settings) {
        return saveSettings("EMAIL", settings);
    }

    /**
     * 測試郵件設置
     */
    public void testEmailSettings(String testEmail) {
        // TODO: 實現郵件測試功能
        log.info("測試郵件發送到: {}", testEmail);
    }

    /**
     * 獲取通知設置
     */
    public Map<String, Object> getNotificationSettings() {
        return getSettingsByType("NOTIFICATION");
    }

    /**
     * 更新通知設置
     */
    @Transactional
    public Map<String, Object> updateNotificationSettings(Map<String, Object> settings) {
        return saveSettings("NOTIFICATION", settings);
    }

    /**
     * 獲取安全設置
     */
    public Map<String, Object> getSecuritySettings() {
        return getSettingsByType("SECURITY");
    }

    /**
     * 更新安全設置
     */
    @Transactional
    public Map<String, Object> updateSecuritySettings(Map<String, Object> settings) {
        return saveSettings("SECURITY", settings);
    }

    /**
     * 獲取所有設置
     */
    public Map<String, Map<String, Object>> getAllSettings() {
        Map<String, Map<String, Object>> allSettings = new HashMap<>();
        allSettings.put("system", getSystemSettings());
        allSettings.put("email", getEmailSettings());
        allSettings.put("notification", getNotificationSettings());
        allSettings.put("security", getSecuritySettings());
        return allSettings;
    }

    /**
     * 重置為默認設置
     */
    @Transactional
    public void resetToDefaults() {
        settingsRepository.deleteAll();
        // 創建默認設置
        createDefaultSettings();
    }

    /**
     * 根據類型獲取設置
     */
    private Map<String, Object> getSettingsByType(String type) {
        List<Settings> settingsList = settingsRepository.findBySettingType(type);
        if (settingsList.isEmpty()) {
            return createDefaultSettingsForType(type);
        }
        
        Map<String, Object> result = new HashMap<>();
        for (Settings setting : settingsList) {
            try {
                Object value = objectMapper.readValue(setting.getSettingValue(), Object.class);
                result.put(setting.getSettingKey(), value);
            } catch (Exception e) {
                log.error("解析設置值失敗: {}", setting.getSettingKey(), e);
                result.put(setting.getSettingKey(), setting.getSettingValue());
            }
        }
        return result;
    }

    /**
     * 保存設置
     */
    private Map<String, Object> saveSettings(String type, Map<String, Object> settings) {
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            Settings setting = settingsRepository
                    .findBySettingTypeAndSettingKey(type, entry.getKey())
                    .orElse(Settings.builder()
                            .settingType(type)
                            .settingKey(entry.getKey())
                            .build());
            
            try {
                String value = objectMapper.writeValueAsString(entry.getValue());
                setting.setSettingValue(value);
                settingsRepository.save(setting);
            } catch (Exception e) {
                log.error("保存設置失敗: {}", entry.getKey(), e);
            }
        }
        return getSettingsByType(type);
    }

    /**
     * 創建默認設置
     */
    private void createDefaultSettings() {
        createDefaultSettingsForType("SYSTEM");
        createDefaultSettingsForType("EMAIL");
        createDefaultSettingsForType("NOTIFICATION");
        createDefaultSettingsForType("SECURITY");
    }

    /**
     * 為指定類型創建默認設置
     */
    private Map<String, Object> createDefaultSettingsForType(String type) {
        Map<String, Object> defaults = new HashMap<>();
        
        switch (type) {
            case "SYSTEM":
                defaults.put("storeName", "Shopro 電商平台");
                defaults.put("storeDescription", "");
                defaults.put("storeEmail", "");
                defaults.put("storePhone", "");
                defaults.put("storeAddress", "");
                defaults.put("currency", "TWD");
                defaults.put("timezone", "Asia/Taipei");
                defaults.put("language", "zh-TW");
                defaults.put("taxRate", 0);
                defaults.put("shippingFeeRate", 0);
                defaults.put("enableNotification", true);
                defaults.put("enableEmail", true);
                defaults.put("enableSMS", false);
                defaults.put("maintenanceMode", false);
                break;
            case "EMAIL":
                defaults.put("smtpServer", "");
                defaults.put("smtpPort", 587);
                defaults.put("smtpUsername", "");
                defaults.put("smtpPassword", "");
                defaults.put("emailFrom", "");
                defaults.put("emailReplyTo", "");
                defaults.put("enableTLS", true);
                defaults.put("enableSSL", false);
                break;
            case "NOTIFICATION":
                defaults.put("orderNotification", true);
                defaults.put("paymentNotification", true);
                defaults.put("userRegistrationNotification", false);
                defaults.put("lowStockNotification", true);
                defaults.put("notificationEmail", "");
                defaults.put("notificationPhone", "");
                break;
            case "SECURITY":
                defaults.put("passwordMinLength", 6);
                defaults.put("passwordMaxLength", 32);
                defaults.put("requireUppercase", true);
                defaults.put("requireLowercase", true);
                defaults.put("requireNumbers", true);
                defaults.put("requireSpecialChars", false);
                defaults.put("sessionTimeout", 30);
                defaults.put("twoFactorAuthEnabled", false);
                break;
        }
        
        // 保存默認設置
        saveSettings(type, defaults);
        return defaults;
    }
}

