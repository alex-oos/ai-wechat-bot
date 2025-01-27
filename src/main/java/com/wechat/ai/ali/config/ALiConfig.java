package com.wechat.ai.ali.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author Alex
 * @since 2025/1/26 17:55
 * <p></p>
 */
@Data
@Component
@Configurable
@ConfigurationProperties(prefix = "ai.ali")
public class ALiConfig {


    private String model;

    private String apiKey;

    private Boolean enabled;

    private String name;


}
