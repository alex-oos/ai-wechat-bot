package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.service.LoginService;
import com.wechat.config.SystemConfig;
import com.wechat.gewechat.service.ContactApi;
import com.wechat.gewechat.service.LoginApi;
import com.wechat.util.FileUtil;
import com.wechat.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Alex
 * @since 2025/1/27 12:01
 * <p></p>
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    SystemConfig systemConfig;

    @Override
    public void login() {

        //检查设施是否在线
        JSONObject checkOnline = LoginApi.checkOnline(systemConfig.getAppId());
        if (checkOnline.getInteger("ret") == 200 && checkOnline.getBoolean("data")) {
            log.info("AppId : {} 已在线，无需登录", systemConfig.getAppId());
            return;
        }
        log.info("APPid:{}", "并未在线，开始执行登录流程");
        systemConfig.setAppId(null);
        systemConfig.setToken(null);
        this.getToken();
        String uuid = this.getqr();
        this.checkQr(uuid);


    }

    @Override
    public String getqr() {

        String appId = "";
        String uuid = "";
        JSONObject response = LoginApi.getQr("");
        if (response.getInteger("ret") == 200) {
            JSONObject data = response.getJSONObject("data");
            appId = data.getString("appId");
            uuid = data.getString("uuid");
            String qrData = data.getString("qrData");
            System.out.println("时间等待20s，等待你登录");
            System.out.println("请访问下面地址：登录也可以");
            System.out.println("https://api.qrserver.com/v1/create-qr-code/?data=" + qrData);
            systemConfig.setAppId(appId);
            FileUtil.writeFile(systemConfig);
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return uuid;
        }


        return null;


    }

    @Override
    public void getToken() {
        // 删除文件
        FileUtil.configFilePath.toFile().delete();
        /**
         * 1.获取token
         */
        JSONObject response = LoginApi.getToken();
        if (response.getInteger("ret") == 200) {
            String token = response.getString("data");
            if (token != null) {
                systemConfig.setToken(token);
                FileUtil.writeFile(systemConfig);

            }
        }


    }

    @Override
    public void checkQr(String uuid) {

        JSONObject jsonObject = LoginApi.checkQr(systemConfig.getAppId(), uuid, null);
        // 状态不为2的时候才是登录成功
        if (jsonObject.getInteger("ret") == 200 && jsonObject.getJSONObject("data").getInteger("status") != 2) {
            while (true) {
                this.checkQr(this.getqr());
            }
        } else {
            log.info("登录成功");

        }
    }


    @Override
    public void setCallbackUrl() {

        String callbackUrl = "http://" + IpUtil.getIp() + ":9919/v2/api/callback/collect";

        // 设置一下回调地址
        //System.out.println(callbackUrl);
        JSONObject setCallback = LoginApi.setCallback(systemConfig.getToken(), callbackUrl);
        if (setCallback.getInteger("ret") != 200) {
            throw new RuntimeException("设置回调地址失败");
        }
        log.info("设置回调地址成功");
    }

    @Async
    @Override
    public void getALLFriends() {

        JSONObject res = ContactApi.fetchContactsList(systemConfig.getAppId());
        if (res.getInteger("ret") != 200) {
            return;
        }

        JSONObject data = res.getJSONObject("data");

        JSONArray contactList = data.getJSONArray("friends");

        List<String> friends = contactList.toJavaList(String.class);
        JSONArray jsonArray = data.getJSONArray("chatrooms");
        List<String> chatrooms = jsonArray.toJavaList(String.class);

        JSONObject detailInfo = ContactApi.getDetailInfo(systemConfig.getAppId(), friends);
        if (detailInfo.getInteger("ret") != 200) {
            return;
        }
        log.info("获取好友列表成功");
        // 将好友信息保存到数据库中，方便下次直接使用
        ContactApi.getDetailInfo(systemConfig.getAppId(), chatrooms);
        // 将好友信息保存到数据库中，方便下次直接使用


    }


}
