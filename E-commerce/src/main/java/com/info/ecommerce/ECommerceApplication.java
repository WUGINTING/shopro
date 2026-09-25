package com.info.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ECommerceApplication {

    public static void main(String[] args) {
        // 營業時間、促銷 / 廣告期間、統計的日期都以商店所在時區計算；主機預設為 UTC 時也不會差 8 小時
        String zone = System.getenv().getOrDefault("APP_TIMEZONE", "Asia/Taipei");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone(zone));
        SpringApplication.run(ECommerceApplication.class, args);
    }

}
