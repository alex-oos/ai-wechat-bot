package com.wechat.bot.config;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.gewechat.service.LoginApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * @author Alex
 * @since 2025/1/26 16:54
 * <p>
 * 服务起来之后，自动设置回调地址
 * </p>
 */
@Slf4j
@Component
public class CallBackUrlConfig implements ApplicationRunner {

    @Autowired
    private SystemConfig systemConfig;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 设置一下回调地址
        JSONObject setCallback = LoginApi.setCallback(systemConfig.getToken(), systemConfig.getCallbackUrl());
        if (setCallback.getInteger("ret") != 200) {
            throw new RuntimeException("设置回调地址失败");
        }
        log.info("设置回调地址成功");

    }

}
