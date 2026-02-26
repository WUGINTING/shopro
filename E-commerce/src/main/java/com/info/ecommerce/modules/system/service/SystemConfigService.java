package com.info.ecommerce.modules.system.service;

import com.info.ecommerce.modules.system.dto.StoreContentConfigDTO;
import com.info.ecommerce.modules.system.dto.SystemConfigDTO;
import com.info.ecommerce.modules.system.entity.SystemConfig;
import com.info.ecommerce.modules.system.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    private static final String DEFAULT_SITE_NAME = "Shopro 電商平台";
    private static final String DEFAULT_CURRENCY = "TWD";
    private static final java.math.BigDecimal DEFAULT_TAX_RATE = java.math.BigDecimal.valueOf(5.00);
    private static final Integer DEFAULT_ORDER_CANCEL_MINUTES = 30;
    private static final String DEFAULT_STOCK_DEDUCTION_TIMING = "PAYMENT_COMPLETED";
    private static final Boolean DEFAULT_REQUIRE_MEMBER_APPROVAL = false;
    private static final Integer DEFAULT_POINTS_EXPIRATION_DAYS = 365;
    private static final Integer DEFAULT_MIN_PASSWORD_LENGTH = 8;

    public SystemConfigDTO getSystemConfig() {
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdDesc().orElse(null);
        if (config == null) {
            return createDefaultConfig();
        }
        return toDTO(config);
    }

    @Transactional
    public SystemConfigDTO saveSystemConfig(SystemConfigDTO dto) {
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdDesc().orElse(new SystemConfig());
        BeanUtils.copyProperties(dto, config, "id", "createdAt", "updatedAt");
        return toDTO(systemConfigRepository.save(config));
    }

    public StoreContentConfigDTO getStoreContentConfig() {
        return systemConfigRepository.findFirstByOrderByIdDesc()
                .map(this::toStoreContentDTO)
                .orElseGet(this::createDefaultStoreContentConfig);
    }

    @Transactional
    public StoreContentConfigDTO saveStoreContentConfig(StoreContentConfigDTO dto) {
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdDesc()
                .orElseGet(this::createDefaultSystemConfigEntity);

        config.setBrandStoryBadge(dto.getBrandStoryBadge());
        config.setBrandStoryTitle(dto.getBrandStoryTitle());
        config.setBrandStoryLead(dto.getBrandStoryLead());
        config.setBrandMissionTitle(dto.getBrandMissionTitle());
        config.setBrandMissionContent(dto.getBrandMissionContent());
        config.setBrandVisionTitle(dto.getBrandVisionTitle());
        config.setBrandVisionContent(dto.getBrandVisionContent());
        config.setBrandValueTitle(dto.getBrandValueTitle());
        config.setBrandValueContent(dto.getBrandValueContent());
        config.setBrandStoryNote(dto.getBrandStoryNote());

        config.setContactPageBadge(dto.getContactPageBadge());
        config.setContactPageTitle(dto.getContactPageTitle());
        config.setContactPageLead(dto.getContactPageLead());
        config.setContactEmail(dto.getContactEmail());
        config.setContactEmailHint(dto.getContactEmailHint());
        config.setContactPhone(dto.getContactPhone());
        config.setContactPhoneHint(dto.getContactPhoneHint());
        config.setBusinessHours(dto.getBusinessHours());
        config.setContactBusinessHoursHint(dto.getContactBusinessHoursHint());
        config.setAddress(dto.getAddress());
        config.setContactAddressHint(dto.getContactAddressHint());
        config.setContactSupportNote(dto.getContactSupportNote());

        return toStoreContentDTO(systemConfigRepository.save(config));
    }

    private SystemConfigDTO createDefaultConfig() {
        return SystemConfigDTO.builder()
                .siteName(DEFAULT_SITE_NAME)
                .defaultCurrency(DEFAULT_CURRENCY)
                .taxRate(DEFAULT_TAX_RATE)
                .autoOrderCancelMinutes(DEFAULT_ORDER_CANCEL_MINUTES)
                .stockDeductionTiming(DEFAULT_STOCK_DEDUCTION_TIMING)
                .requireMemberApproval(DEFAULT_REQUIRE_MEMBER_APPROVAL)
                .pointsExpirationDays(DEFAULT_POINTS_EXPIRATION_DAYS)
                .minPasswordLength(DEFAULT_MIN_PASSWORD_LENGTH)
                .build();
    }

    private StoreContentConfigDTO createDefaultStoreContentConfig() {
        return StoreContentConfigDTO.builder()
                .brandStoryBadge("品牌故事")
                .brandStoryTitle("從選品到出貨，打造讓人安心回購的購物體驗")
                .brandStoryLead("Shopro 以透明、效率與服務為核心，協助消費者快速找到合適商品，並提供穩定的售後支援。")
                .brandMissionTitle("我們的使命")
                .brandMissionContent("用更清楚的資訊、更穩定的出貨流程，降低消費者做決定的成本。")
                .brandVisionTitle("我們的願景")
                .brandVisionContent("成為值得信任的線上選品平台，讓每次購買都更輕鬆。")
                .brandValueTitle("我們重視的價值")
                .brandValueContent("誠實溝通、品質把關、服務效率，以及持續優化購物流程。")
                .brandStoryNote("若你對合作、選品或品牌提案有想法，歡迎透過聯絡我們與團隊討論。")
                .contactPageBadge("聯絡我們")
                .contactPageTitle("需要協助嗎？我們會盡快回覆")
                .contactPageLead("訂單、商品、售後與合作問題都可以透過以下方式聯繫，我們會在服務時間內處理。")
                .contactEmail("support@shopro.example")
                .contactEmailHint("建議來信附上訂單編號與問題描述，可加速處理。")
                .contactPhone("(02) 1234-5678")
                .contactPhoneHint("客服時段來電可獲得較快回覆。")
                .businessHours("週一至週五 10:00 - 18:00")
                .contactBusinessHoursHint("國定假日與例假日暫停服務。")
                .address("台北市信義區市府路 1 號")
                .contactAddressHint("如需退換貨或合作寄件，請先與客服確認收件資訊。")
                .contactSupportNote("我們重視每一則訊息，若遇到尖峰時段回覆較慢，敬請見諒。")
                .build();
    }

    private SystemConfig createDefaultSystemConfigEntity() {
        return SystemConfig.builder()
                .siteName(DEFAULT_SITE_NAME)
                .defaultCurrency(DEFAULT_CURRENCY)
                .taxRate(DEFAULT_TAX_RATE)
                .autoOrderCancelMinutes(DEFAULT_ORDER_CANCEL_MINUTES)
                .stockDeductionTiming(DEFAULT_STOCK_DEDUCTION_TIMING)
                .requireMemberApproval(DEFAULT_REQUIRE_MEMBER_APPROVAL)
                .pointsExpirationDays(DEFAULT_POINTS_EXPIRATION_DAYS)
                .minPasswordLength(DEFAULT_MIN_PASSWORD_LENGTH)
                .build();
    }

    private StoreContentConfigDTO toStoreContentDTO(SystemConfig entity) {
        StoreContentConfigDTO defaults = createDefaultStoreContentConfig();
        return StoreContentConfigDTO.builder()
                .brandStoryBadge(valueOrDefault(entity.getBrandStoryBadge(), defaults.getBrandStoryBadge()))
                .brandStoryTitle(valueOrDefault(entity.getBrandStoryTitle(), defaults.getBrandStoryTitle()))
                .brandStoryLead(valueOrDefault(entity.getBrandStoryLead(), defaults.getBrandStoryLead()))
                .brandMissionTitle(valueOrDefault(entity.getBrandMissionTitle(), defaults.getBrandMissionTitle()))
                .brandMissionContent(valueOrDefault(entity.getBrandMissionContent(), defaults.getBrandMissionContent()))
                .brandVisionTitle(valueOrDefault(entity.getBrandVisionTitle(), defaults.getBrandVisionTitle()))
                .brandVisionContent(valueOrDefault(entity.getBrandVisionContent(), defaults.getBrandVisionContent()))
                .brandValueTitle(valueOrDefault(entity.getBrandValueTitle(), defaults.getBrandValueTitle()))
                .brandValueContent(valueOrDefault(entity.getBrandValueContent(), defaults.getBrandValueContent()))
                .brandStoryNote(valueOrDefault(entity.getBrandStoryNote(), defaults.getBrandStoryNote()))
                .contactPageBadge(valueOrDefault(entity.getContactPageBadge(), defaults.getContactPageBadge()))
                .contactPageTitle(valueOrDefault(entity.getContactPageTitle(), defaults.getContactPageTitle()))
                .contactPageLead(valueOrDefault(entity.getContactPageLead(), defaults.getContactPageLead()))
                .contactEmail(valueOrDefault(entity.getContactEmail(), defaults.getContactEmail()))
                .contactEmailHint(valueOrDefault(entity.getContactEmailHint(), defaults.getContactEmailHint()))
                .contactPhone(valueOrDefault(entity.getContactPhone(), defaults.getContactPhone()))
                .contactPhoneHint(valueOrDefault(entity.getContactPhoneHint(), defaults.getContactPhoneHint()))
                .businessHours(valueOrDefault(entity.getBusinessHours(), defaults.getBusinessHours()))
                .contactBusinessHoursHint(valueOrDefault(entity.getContactBusinessHoursHint(), defaults.getContactBusinessHoursHint()))
                .address(valueOrDefault(entity.getAddress(), defaults.getAddress()))
                .contactAddressHint(valueOrDefault(entity.getContactAddressHint(), defaults.getContactAddressHint()))
                .contactSupportNote(valueOrDefault(entity.getContactSupportNote(), defaults.getContactSupportNote()))
                .build();
    }

    private String valueOrDefault(String value, String defaultValue) {
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private SystemConfigDTO toDTO(SystemConfig entity) {
        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
