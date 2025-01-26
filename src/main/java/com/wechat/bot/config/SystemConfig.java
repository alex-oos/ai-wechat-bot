package com.wechat.bot.config;

import com.wechat.bot.util.FileUtil;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Alex
 * @since 2025/1/26 14:34
 * <p></p>
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SystemConfig {


    private String token;

    private String appId;

    private String callbackUrl;

    private String downloadUrl;

    private String baseUrl;



}
