package com.wechat.bot.config;

import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.service.BotConfigService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author Alex
 * @since 2025/1/30 15:07
 * <p></p>
 */
@Component
@ComponentScan("com.wechat.bot.service")
public class MyConfig {


    @Bean
    @DependsOn("sqliteSchemaInit")
    public BotConfig init(BotConfigService botConfigService) {
        return botConfigService.loadOrSeed();
    }


}
