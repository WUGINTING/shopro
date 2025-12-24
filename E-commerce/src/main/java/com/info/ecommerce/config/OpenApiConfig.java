package com.info.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("電商平台系統 API 檔案")
                        .version("1.0")
                        .description("涵蓋商品、訂單、行銷、CRM 等全通路功能接口")
                        .contact(new Contact().name("開發團隊")));
    }
}
