package com.info.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 啟用 @Async（寄送通知信等不阻塞主要流程的工作）
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
