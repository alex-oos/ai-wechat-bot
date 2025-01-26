package com.wechat.bot.config;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.gewechat.service.LoginApi;
import com.wechat.bot.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * @author Alex
 * @since 2025/1/24 11:17
 * <p>
 * 项目启动需要初始化的配置
 * </p>
 */
@Slf4j
@Component
public class SystemInfo {


    @Resource
    SystemConfig systemConfig;

    public void login() {

        //检查设施是否在线
        JSONObject checkOnline = LoginApi.checkOnline(systemConfig.getAppId());
        if (checkOnline.getInteger("ret") == 200) {
            log.info("AppId : {} 已在线，无需登录", systemConfig.getAppId());
            return;
        }
        log.info("APPid:{}", "并未在线，开始执行登录流程");
        /**
         * 1.获取token
         */
        while (true) {
            JSONObject response = LoginApi.getToken();
            if (response.getInteger("ret") == 200) {
                String token = response.getString("data");
                if (token != null) {
                    systemConfig.setToken(token);
                    FileUtil.writeFile(systemConfig);
                }
                break;
            }
        }

        /**
         *3、 获取登录二维码
         * @param appId   设备id 首次登录传空，后续登录传返回的appid
         */
        String appId;
        String uuid;
        while (true) {
            appId = "";
            JSONObject response = LoginApi.getQr("");
            if (response.getInteger("ret") == 200) {
                JSONObject jsonObject1 = response.getJSONObject("data");
                uuid = jsonObject1.getString("uuid");
                String qrData = jsonObject1.getString("qrData");
                System.out.println("请访问下面地址：登录也可以");
                System.out.println("https://api.qrserver.com/v1/create-qr-code/?data=" + qrData);
                systemConfig.setAppId(appId);
                FileUtil.writeFile(systemConfig);
                break;
            }
        }

        /**
         * 4、确认登陆
         * @param appId

         * @param uuid       取码返回的uuid
         * @param captchCode 登录验证码（必须同省登录才能避免此问题，也能使账号更加稳定）
         */
        JSONObject jsonObject = LoginApi.checkQr(appId, uuid, null);
        if (jsonObject.getInteger("ret") != 200) {
            throw new RuntimeException("确认登录失败");

        }
        if (jsonObject.getJSONObject("data").getInteger("status") == 2) {
            log.info("登录成功，appId:{}", appId);
        }


    }

    @PostConstruct
    public void start() {

        login();

    }


}
