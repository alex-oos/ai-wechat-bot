package com.wechat.bot.config;

import com.wechat.bot.entity.BotConfig;

public class BotConfigHolder {

    private static volatile BotConfig botConfig;

    private BotConfigHolder() {
    }

    public static BotConfig get() {
        return botConfig;
    }

    public static void set(BotConfig config) {
        botConfig = config;
    }
}
