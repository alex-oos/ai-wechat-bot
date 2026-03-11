package com.wechat.admin.controller;

import com.wechat.bot.entity.dto.WechatChannelConfigDTO;
import com.wechat.bot.service.WechatChannelConfigService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config/channel")
public class WechatChannelConfigController {

    private final WechatChannelConfigService service;

    public WechatChannelConfigController(WechatChannelConfigService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> getConfig() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("config", service.getConfig());
        return res;
    }

    @PutMapping
    public Map<String, Object> updateConfig(@RequestBody UpdateRequest req) {
        WechatChannelConfigDTO updates = new WechatChannelConfigDTO();
        updates.setChannelType(req.getChannelType());
        updates.setBaseUrl(req.getBaseUrl());
        updates.setCallbackUrl(req.getCallbackUrl());
        updates.setDownloadUrl(req.getDownloadUrl());
        updates.setAppId(req.getAppId());
        updates.setToken(req.getToken());
        WechatChannelConfigDTO saved = service.updateConfig(updates);
        return Map.of("success", true, "config", saved);
    }

    @Data
    public static class UpdateRequest {
        private String channelType;
        private String baseUrl;
        private String callbackUrl;
        private String downloadUrl;
        private String appId;
        private String token;
    }
}
