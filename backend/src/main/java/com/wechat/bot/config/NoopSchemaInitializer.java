package com.wechat.bot.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!sqlite")
public class NoopSchemaInitializer {

    @Bean("sqliteSchemaInit")
    public InitializingBean sqliteSchemaInit() {
        return () -> {
            // no-op for non-sqlite profiles
        };
    }
}
