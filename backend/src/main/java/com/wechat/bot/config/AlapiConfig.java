package com.wechat.bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alex
 * @since 2025/4/15 16:37
 * <p>
 * 官网地址：
 * https://www.alapi.cn
 * </p>
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "happy.alapi")
public class AlapiConfig {

    private String host;

    private String token;

}
