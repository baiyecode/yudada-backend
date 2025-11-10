package com.baiye.yudada.config;

import ai.z.openapi.ZhipuAiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * AI 配置类
 */
@Configuration
@ConfigurationProperties(prefix = "ai") // 指定配置文件前缀
@Data
public class AiConfig {

    //apikey,要从智谱ai获取
    private String apiKey;

    @Bean
    public ZhipuAiClient getZhipuAiClient() {
        // 初始化客户端
        return ZhipuAiClient.builder()
                .apiKey(apiKey)
                .build();
    }
}
