package com.wechat.admin.service;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.gewechat.service.LoginApi;
import com.wechat.gewechat.util.OkhttpUtil;
import com.wechat.bot.entity.dto.WechatChannelConfigDTO;
import com.wechat.bot.service.WechatChannelConfigService;
import com.wechat.util.QRCodeImageUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WechatLoginHttpService {

    private final WechatChannelConfigService channelConfigService;

    public WechatLoginHttpService(WechatChannelConfigService channelConfigService) {
        this.channelConfigService = channelConfigService;
    }

    public Map<String, Object> getQr() {
        ensureBaseUrl();
        ensureToken();

        WechatChannelConfigDTO config = channelConfigService.getConfig();
        String appId = config.getAppId();
        if (appId == null) {
            appId = "";
        }

        JSONObject response = LoginApi.getQr(appId);
        if (response.getInteger("ret") != 200) {
            throw new RuntimeException("获取二维码失败: " + response.toJSONString());
        }

        JSONObject data = response.getJSONObject("data");
        String newAppId = data.getString("appId");
        String uuid = data.getString("uuid");
        String qrData = data.getString("qrData");

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("appId", newAppId);
        res.put("uuid", uuid);
        res.put("qrData", qrData);
        res.put("qrImage", QRCodeImageUtil.toPngDataUrlBase64(qrData, 260));
        return res;
    }

    public Map<String, Object> checkQr(String appId, String uuid, String captchCode) {
        ensureBaseUrl();
        ensureToken();

        JSONObject response = LoginApi.checkQr(appId, uuid, captchCode);
        if (response.getInteger("ret") != 200) {
            return Map.of("success", false, "message", response.getString("msg"), "raw", response);
        }

        JSONObject data = response.getJSONObject("data");
        Integer status = data.getInteger("status");
        Integer expiredTime = data.getInteger("expiredTime");
        String nickName = data.getString("nickName");

        boolean loggedIn = status != null && status == 2;
        if (loggedIn) {
            WechatChannelConfigDTO updates = new WechatChannelConfigDTO();
            updates.setAppId(appId);
            channelConfigService.updateConfig(updates);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("status", status);
        res.put("expiredTime", expiredTime);
        res.put("nickName", nickName);
        res.put("loggedIn", loggedIn);
        return res;
    }

    private void ensureBaseUrl() {
        WechatChannelConfigDTO config = channelConfigService.getConfig();
        OkhttpUtil.baseUrl = config.getBaseUrl();
        if (OkhttpUtil.baseUrl == null || OkhttpUtil.baseUrl.isBlank()) {
            throw new RuntimeException("baseUrl 未配置，请先在配置页面中配置 gewechat baseUrl");
        }
    }

    private void ensureToken() {
        WechatChannelConfigDTO config = channelConfigService.getConfig();
        if (config.getToken() != null && !config.getToken().isBlank()) {
            OkhttpUtil.token = config.getToken();
            return;
        }
        JSONObject tokenRes = LoginApi.getToken();
        if (tokenRes.getInteger("ret") != 200) {
            throw new RuntimeException("获取 token 失败: " + tokenRes.toJSONString());
        }
        String token = tokenRes.getString("data");
        OkhttpUtil.token = token;
        WechatChannelConfigDTO updates = new WechatChannelConfigDTO();
        updates.setToken(token);
        channelConfigService.updateConfig(updates);
    }
}
