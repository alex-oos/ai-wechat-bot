package com.wechat.admin.controller;

import com.wechat.admin.service.WechatLoginHttpService;
import com.wechat.bot.service.WechatChannelConfigService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.net.SocketException;
import java.util.Map;

@RestController
@RequestMapping("/api/wechat")
public class WechatLoginController {

    private final WechatLoginHttpService loginHttpService;
    private final WechatChannelConfigService channelConfigService;

    public WechatLoginController(WechatLoginHttpService loginHttpService, WechatChannelConfigService channelConfigService) {
        this.loginHttpService = loginHttpService;
        this.channelConfigService = channelConfigService;
    }

    @GetMapping("/qr")
    public Map<String, Object> getQr() {
        try {
            return loginHttpService.getQr();
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            Throwable c = e.getCause();
            while (c != null && c.getCause() != c) {
                if (c instanceof SocketException) break;
                c = c.getCause();
            }
            if (c instanceof SocketException && (msg == null || msg.isBlank())) {
                msg = c.getMessage();
            }
            return Map.of(
                    "success", false,
                    "message", msg == null ? "gewechat 请求失败" : msg,
                    "baseUrl", channelConfigService.getConfig().getBaseUrl(),
                    "hint", "请确认“微信接入渠道”页面的 baseUrl 指向可访问的 gewechat（同网段/同省要求），可通过 PUT /api/config/channel 更新 baseUrl"
            );
        }
    }

    @PostMapping("/qr/status")
    public Map<String, Object> checkQr(@RequestBody CheckRequest req) {
        try {
            return loginHttpService.checkQr(req.getAppId(), req.getUuid(), req.getCaptchCode());
        } catch (RuntimeException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage() == null ? "gewechat 请求失败" : e.getMessage(),
                    "baseUrl", channelConfigService.getConfig().getBaseUrl()
            );
        }
    }

    @Data
    public static class CheckRequest {
        private String appId;
        private String uuid;
        private String captchCode;
    }
}
