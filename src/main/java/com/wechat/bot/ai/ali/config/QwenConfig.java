package com.wechat.bot.ai.ali.config;


import com.wechat.bot.fatory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource(value = {"classpath:static/ai.yml"}, encoding = "UTF-8", factory = YamlPropertySourceFactory.class)
public class QwenConfig {


    //private String model = "qwen-plus";
    private String model = "";

    private String apiKey = "";
    //private String apiKey = "sk-4d61e913e8cf4185a816275dae53309b";

    private boolean enabled;


}
