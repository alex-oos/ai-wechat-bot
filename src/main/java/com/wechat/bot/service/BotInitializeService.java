package com.wechat.bot.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
public class BotInitializeService implements InitializingBean {


    @Resource
    private LoginService loginService;


    @Override
    public void afterPropertiesSet() throws Exception {

        loginService.login();
        TimeUnit.SECONDS.sleep(5);
        loginService.setCallbackUrl();

    }

}
