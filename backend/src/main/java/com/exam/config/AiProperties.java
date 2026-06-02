package com.exam.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 服务配置
 */
@Data
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {
    private String baseUrl;
    private String apiKey;
    private String model = "gpt-5.4";
    private long timeout = 30000;
}
