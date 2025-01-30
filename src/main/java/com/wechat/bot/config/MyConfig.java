package com.wechat.bot.config;

import com.wechat.bot.entity.BotConfig;
import com.wechat.util.FileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Alex
 * @since 2025/1/30 15:07
 * <p></p>
 */
@Component
public class MyConfig {

    @Bean
    public BotConfig init() {
        return FileUtil.readFile();
    }

}
