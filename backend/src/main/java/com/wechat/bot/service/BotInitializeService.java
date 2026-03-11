package com.wechat.bot.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * @author Alex
 * @since 2025/1/26 16:54
 * <p>
 * 服务自动初始化，登录微信，并且设置回调地址
 * </p>
 */
@Slf4j
@Component
public class BotInitializeService implements ApplicationRunner {

    @Value("${bot.autoInit:true}")
    private boolean autoInit;


    @Resource
    FriendService friendService;

    @Resource
    private LoginService loginService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!autoInit) {
            log.info("bot.autoInit=false，跳过启动时自动登录与回调设置");
            return;
        }

        try {
            loginService.login();
            TimeUnit.SECONDS.sleep(5);
            loginService.setCallbackUrl();
        } catch (Exception e) {
            // 作为后端服务运行时，启动不应因外部 gewechat 不可用而退出
            log.error("启动初始化失败（gewechat 不可用或配置错误），服务将继续运行。你可稍后通过管理页重新扫码登录。", e);
        }
        //首次登录的时候同步一下好友信息
        //friendService.syncContacts();


    }


}
