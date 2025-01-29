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

        try {
            // 从数据库中获取最新的系统配置
            LambdaQueryWrapper<SystemConfigDto> queryWrapper = new QueryWrapper<SystemConfigDto>().lambda();
            queryWrapper.orderByDesc(SystemConfigDto::getId);
            SystemConfigDto systemConfig = userService.getOne(queryWrapper);
            if (systemConfig != null) {
                handleExistingConfig(systemConfig);
            } else {
                handleNewConfig();
            }
        } catch (Exception e) {
            log.error("获取系统配置失败", e);
            // 删除所有的数据
            userService.remove(new QueryWrapper<>());
            log.error("已经删除所有的配置文件，请重新启动尝试一下，即可");
        }


    }


    /**
     * 处理已存在配置的情况
     */
    private void handleExistingConfig(SystemConfigDto existingConfig) {
        // 检查设施是否在线
        OkhttpUtil.token = existingConfig.getToken();
        JSONObject onlineStatus = LoginApi.checkOnline(existingConfig.getAppId());

        if (isStillOnline(onlineStatus)) {
            log.info("AppId : {} 保持在线状态，跳过登录流程", existingConfig.getAppId());
            return;
        }
        log.info("AppId : {} 已离线，开始执行登录流程", existingConfig.getAppId());
        performLoginFlow(existingConfig.getAppId(), true, existingConfig);

    }

    /**
     * 处理新配置的情况
     */
    private void handleNewConfig() {

        log.info("系统初次登录，开始初始化流程");
        this.getToken();  // 确保先获取基础token
        performLoginFlow("", false, null);
    }

    /**
     * 执行完整的登录流程
     *
     * @param appId            当前appId（可能为空）
     * @param isExistingConfig 是否是已存在的配置
     * @param originalConfig   原始配置对象（更新时使用）
     */
    private void performLoginFlow(String appId, boolean isExistingConfig, SystemConfigDto originalConfig) {
        // 获取登录二维码
        Map<String, String> qrInfo = this.getqr(appId);

        // 执行二维码验证流程
        this.checkStatus(qrInfo);

        // 构建需要保存的配置对象
        SystemConfigDto configToSave = buildConfigDto(qrInfo, isExistingConfig, originalConfig);

        //持久化到数据库
        userService.saveOrUpdate(configToSave);
    }

    /**
     * 构建配置对象
     */
    private SystemConfigDto buildConfigDto(Map<String, String> qrInfo, boolean isExistingConfig, SystemConfigDto originalConfig) {

        SystemConfigDto.SystemConfigDtoBuilder builder = SystemConfigDto.builder().token(OkhttpUtil.token);

        if (isExistingConfig) {
            // 更新一下update_time 时间即可
            return builder.loginTime(new Date()).build();
        } else {
            // 新建时设置所有字段
            return builder.appId(qrInfo.get("appId")).createTime(new Date()).loginTime(new Date()).build();
        }
    }

    /**
     * 判断是否保持在线状态
     */
    private boolean isStillOnline(JSONObject response) {

        return response.getInteger("ret") == 200 && response.getBoolean("data");
    }

    @Override
    public Map<String, String> getqr(String appId) {

        JSONObject response = LoginApi.getQr(appId);
        Map<String, String> map = new HashMap<>();
        if (response.getInteger("ret") == 200) {
            JSONObject data = response.getJSONObject("data");
            map.put("appId", data.getString("appId"));
            map.put("uuid", data.getString("uuid"));
            String qrData = data.getString("qrData");
            System.out.println("请访问下面地址：登录也可以");
            System.out.println("https://api.qrserver.com/v1/create-qr-code/?data=" + qrData);
            return map;
        }

        return Map.of();


    }

    @Override
    public void getToken() {
        /**
         * 1.获取token
         */
        JSONObject response = LoginApi.getToken();
        if (response.getInteger("ret") == 200) {
            OkhttpUtil.token = response.getString("data");
        }


    }

    @Override
    public void checkStatus(Map<String, String> map) {

        int retryCount = 0;

        int maxRetries = 100; // 最大重试100次

        while (retryCount < maxRetries) {
            JSONObject response = LoginApi.checkQr(map.get("appId"), map.get("uuid"), null);
            if (response.getInteger("ret") != 200) {
                log.error("检查登录状态失败:{}", response.getString("msg"));
                return;
            }
            //2. 获取解析数据
            JSONObject data = response.getJSONObject("data");
            int statusCode = data.getInteger("status");
            int expiredTime = data.getInteger("expiredTime");
            //3.处理二维码过期
            //轮询间隔5s
            if (expiredTime <= 5) {
                System.out.println("二维码即将过期，重新获取...");
                this.getqr(map.get("appId"));
                LoginApi.checkQr(map.get("appId"), map.get("uuid"), null);
                continue;
            }

            //4.处理状态问题
            if (statusCode == 2) {
                String nickName = data.getString("nickName");
                log.info("登录成功,用户昵称是：" + nickName);

            } else {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("登录超时，请重新尝试");
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


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
