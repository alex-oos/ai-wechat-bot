package com.wechat.ai.config;

import com.wechat.bot.entity.BotConfig;
import com.wechat.util.FileUtil;
import lombok.Getter;

/**
 * @author Alex
 * @since 2025/3/12 21:00
 * <p></p>
 */
@Getter
public class AiConfig {

    public static BotConfig botConfig;

    static {
        botConfig = FileUtil.readFile();
    }

}
