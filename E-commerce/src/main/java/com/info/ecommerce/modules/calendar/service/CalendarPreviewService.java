package com.info.ecommerce.modules.calendar.service;

import com.info.ecommerce.modules.calendar.dto.CalendarPreviewDTO;
import com.info.ecommerce.modules.calendar.entity.CalendarEvent;
import com.info.ecommerce.modules.calendar.enums.CalendarEventType;
import com.info.ecommerce.modules.calendar.repository.CalendarEventRepository;
import com.info.ecommerce.modules.product.repository.ProductRepository;
import com.info.ecommerce.modules.marketing.repository.MarketingCampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarPreviewService {

    private final CalendarEventRepository eventRepository;
    private final ProductRepository productRepository;
    private final MarketingCampaignRepository campaignRepository;

    /**
     * 預覽特定時間點的效果
     */
    public CalendarPreviewDTO previewTimePoint(LocalDateTime previewTime) {
        CalendarPreviewDTO preview = new CalendarPreviewDTO();
        preview.setPreviewTime(previewTime);

        // 查詢該時間點的所有事件
        List<CalendarEvent> events = eventRepository.findEventsInTimeRange(
                previewTime,
                previewTime
        );

        List<CalendarPreviewDTO.ProductPreviewDTO> listedProducts = new ArrayList<>();
        List<CalendarPreviewDTO.ActivityPreviewDTO> activeActivities = new ArrayList<>();

        for (CalendarEvent event : events) {
            if (event.getType() == CalendarEventType.PRODUCT_LISTING && 
                event.getEntityType().equals("PRODUCT")) {
                // 查詢商品資訊
                productRepository.findById(event.getEntityId()).ifPresent(product -> {
                    CalendarPreviewDTO.ProductPreviewDTO productPreview = 
                            new CalendarPreviewDTO.ProductPreviewDTO();
                    productPreview.setProductId(product.getId());
                    productPreview.setProductName(product.getName());
                    productPreview.setSku(product.getSku());
                    // 這裡可以從 ProductSpecification 獲取庫存，簡化處理
                    productPreview.setStock(0); // 需要從規格表獲取總庫存
                    listedProducts.add(productPreview);
                });
            } else if (event.getType() == CalendarEventType.MARKETING_ACTIVITY) {
                // 查詢活動資訊
                if (event.getEntityType().equals("MARKETING_CAMPAIGN")) {
                    campaignRepository.findById(event.getEntityId()).ifPresent(campaign -> {
                        CalendarPreviewDTO.ActivityPreviewDTO activityPreview = 
                                new CalendarPreviewDTO.ActivityPreviewDTO();
                        activityPreview.setActivityId(campaign.getId());
                        activityPreview.setActivityName(campaign.getName());
                        activityPreview.setActivityType("MARKETING_CAMPAIGN");
                        activeActivities.add(activityPreview);
                    });
                }
            }
        }

        preview.setListedProducts(listedProducts);
        preview.setActiveActivities(activeActivities);

        // 生成影響說明
        String impactDescription = String.format(
                "預計有 %d 個商品上架，%d 個活動進行中",
                listedProducts.size(),
                activeActivities.size()
        );
        preview.setImpactDescription(impactDescription);

        return preview;
    }
}

