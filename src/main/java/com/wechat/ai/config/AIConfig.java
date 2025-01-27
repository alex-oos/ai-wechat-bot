package com.wechat.ai.config;

import com.wechat.ai.ali.config.ALiConfig;
import com.wechat.ai.xunfei.config.Xunconfig;
import com.wechat.fatory.YamlPropertySourceFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * @author Alex
 * @since 2025/1/27 11:09
 * <p></p>
 */
@Getter
@Component
@Configurable
@ConfigurationProperties(prefix = "ai")
@PropertySource(value = {"classpath:static/ai.yml"}, encoding = "UTF-8", factory = YamlPropertySourceFactory.class)
public class AIConfig {

    private ALiConfig aliConfig;


    private Xunconfig xunfeiConfig;






}
