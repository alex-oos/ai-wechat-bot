package com.wechat.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class WechatBotApplication {

    public static void main(String[] args) {

        SpringApplication.run(WechatBotApplication.class, args);
    }

}
