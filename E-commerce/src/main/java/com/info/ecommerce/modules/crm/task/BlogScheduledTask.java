package com.info.ecommerce.modules.crm.task;

import com.info.ecommerce.modules.crm.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 部落格排程任務
 * 定期檢查並執行排程上架和下架操作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BlogScheduledTask {

    private final BlogService blogService;

    /**
     * 每分鐘檢查一次排程上架的文章
     * cron 表達式：秒 分 時 日 月 星期
     * "0 * * * * ?" 表示每分鐘的第 0 秒執行
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processScheduledPublish() {
        try {
            int count = blogService.processScheduledPublish();
            if (count > 0) {
                log.info("排程上架：成功處理 {} 篇文章", count);
            }
        } catch (Exception e) {
            log.error("排程上架處理失敗", e);
        }
    }

    /**
     * 每分鐘檢查一次排程下架的文章
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processScheduledUnpublish() {
        try {
            int count = blogService.processScheduledUnpublish();
            if (count > 0) {
                log.info("排程下架：成功處理 {} 篇文章", count);
            }
        } catch (Exception e) {
            log.error("排程下架處理失敗", e);
        }
    }
}

