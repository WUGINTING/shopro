package com.info.ecommerce.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "品牌故事與聯絡我們頁面內容設定")
public class StoreContentConfigDTO {

    private String brandStoryBadge;
    private String brandStoryTitle;
    private String brandStoryLead;
    private String brandMissionTitle;
    private String brandMissionContent;
    private String brandVisionTitle;
    private String brandVisionContent;
    private String brandValueTitle;
    private String brandValueContent;
    private String brandStoryNote;

    private String contactPageBadge;
    private String contactPageTitle;
    private String contactPageLead;
    private String contactEmail;
    private String contactEmailHint;
    private String contactPhone;
    private String contactPhoneHint;
    private String businessHours;
    private String contactBusinessHoursHint;
    private String address;
    private String contactAddressHint;
    private String contactSupportNote;
}
