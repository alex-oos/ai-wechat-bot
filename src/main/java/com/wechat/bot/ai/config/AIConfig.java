package com.wechat.bot.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;


/**
 * @author Alex
 * @since 2025/1/27 11:09
 * <p></p>
 */
@Data
@Component
@Configurable
//@PropertySource(value = {"classpath:static/ai.yml"}, encoding = "UTF-8", factory = YamlPropertySourceFactory.class)
public class AIConfig {

    //("${ai.ali.enable}")
    private Boolean aliEnable;







}
