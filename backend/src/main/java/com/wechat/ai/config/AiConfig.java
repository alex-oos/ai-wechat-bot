package com.wechat.ai.config;

import com.wechat.bot.entity.BotConfig;
import lombok.Getter;

/**
 * @author Alex
 * @since 2025/3/12 21:00
 * <p></p>
 */
@Getter
public class AiConfig {

    public static volatile BotConfig botConfig;

    public static void setBotConfig(BotConfig config) {
        botConfig = config;
    }
}
