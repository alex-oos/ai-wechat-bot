package com.wechat.search.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alex
 * @since 2025/4/11 10:08
 * <p></p>
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "ali.search")
public class AliSearchConfig {

    private String host;

    private String apiKey;

}
