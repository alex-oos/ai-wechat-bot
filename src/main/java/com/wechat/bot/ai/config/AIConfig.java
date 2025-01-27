package com.wechat.bot.ai.config;

import com.wechat.bot.ai.ali.config.QwenConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/1/27 11:09
 * <p></p>
 */
@Data
//@Component
//@Configurable
//@PropertySource(value = {"classpath:static/ai.yml"}, encoding = "UTF-8", factory = YmlFactory.class)
public class AIConfig {

    @Resource
    private QwenConfig qwenConfig;


    public AIConfig() {

        qwenConfig.setApiKey("11");

    }

}
