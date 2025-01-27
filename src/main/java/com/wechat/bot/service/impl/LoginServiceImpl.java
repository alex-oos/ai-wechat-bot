package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wechat.bot.entity.dto.FriendDto;
import com.wechat.bot.entity.dto.SystemConfigDto;
import com.wechat.bot.service.FriendService;
import com.wechat.bot.service.LoginService;
import com.wechat.bot.service.SystemConfigService;
import com.wechat.gewechat.service.ContactApi;
import com.wechat.gewechat.service.LoginApi;
import com.wechat.util.FileUtil;
import com.wechat.util.IpUtil;
import com.wechat.util.OkhttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    SystemConfigService userService;

    @Resource
    FriendService friendService;


    @Override
    public void login() {

        LambdaQueryWrapper<SystemConfigDto> queryWrapper = new QueryWrapper<SystemConfigDto>().lambda();
        queryWrapper.orderByDesc(SystemConfigDto::getId);
        SystemConfigDto user = userService.getOne(queryWrapper);
        if (user != null) {
            //检查设施是否在线
            OkhttpUtil.token = user.getToken();
            JSONObject checkOnline = LoginApi.checkOnline(user.getAppId());
            if (checkOnline.getInteger("ret") == 200 && checkOnline.getBoolean("data")) {
                log.info("AppId : {} 已在线，无需登录", user.getAppId());
                return;
            }
        }

        log.info("APPid:{}", "并未在线，开始执行登录流程");
        this.getToken();
        Map<String, String> map = this.getqr();
        this.checkQr(map);
        // 更新数据库里面的数据
        user.setAppId(map.get("appId"));
        user.setToken(OkhttpUtil.token);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userService.saveOrUpdate(user);


    }

    @Override
    public Map<String, String> getqr() {

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
            //systemConfig.setAppId(appId);
            //FileUtil.writeFile(systemConfig);
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Map<String, String> map = new HashMap<>();
            map.put("appId", appId);
            map.put("uuid", uuid);
            return map;
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
                OkhttpUtil.token = token;

            }
        }


    }

    @Override
    public void checkQr(Map<String, String> map) {

        JSONObject jsonObject = LoginApi.checkQr(map.get("appid"), map.get("uuid"), null);
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

        LambdaQueryWrapper<SystemConfigDto> queryWrapper = new QueryWrapper<SystemConfigDto>().lambda();
        queryWrapper.orderByDesc(SystemConfigDto::getId);
        SystemConfigDto user = userService.getOne(queryWrapper);

        String callbackUrl = "http://" + IpUtil.getIp() + ":9919/v2/api/callback/collect";

        // 设置一下回调地址
        //System.out.println(callbackUrl);
        JSONObject setCallback = LoginApi.setCallback(user.getToken(), callbackUrl);
        if (setCallback.getInteger("ret") != 200) {
            throw new RuntimeException("设置回调地址失败");
        }
        log.info("设置回调地址成功");
    }

    @Async
    @Override
    public void getALLFriends() {

        LambdaQueryWrapper<SystemConfigDto> queryWrapper = new QueryWrapper<SystemConfigDto>().lambda();
        queryWrapper.orderByDesc(SystemConfigDto::getId);
        SystemConfigDto user = userService.getOne(queryWrapper);
        JSONObject res = ContactApi.fetchContactsList(user.getAppId());
        if (res.getInteger("ret") != 200) {
            return;
        }

        JSONObject data = res.getJSONObject("data");

        JSONArray contactList = data.getJSONArray("friends");

        List<String> friends = contactList.toJavaList(String.class);
        JSONArray jsonArray = data.getJSONArray("chatrooms");
        List<String> chatrooms = jsonArray.toJavaList(String.class);

        List<String> strings = friends.subList(0, 20);
        JSONObject detailInfo = ContactApi.getDetailInfo(user.getAppId(), strings);
        if (detailInfo.getInteger("ret") != 200) {
            return;
        }
        log.info("获取好友列表成功");

        JSONArray data1 = detailInfo.getJSONArray("data");
        List<FriendDto> javaList = data1.toJavaList(FriendDto.class);

        // 将好友信息保存到数据库中，方便下次直接使用
        JSONObject detailInfo1 = ContactApi.getDetailInfo(user.getAppId(), chatrooms);
        // 将好友信息保存到数据库中，方便下次直接使用


    }


}
