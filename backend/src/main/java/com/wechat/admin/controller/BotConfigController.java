package com.wechat.admin.controller;

import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.service.BotConfigService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class BotConfigController {

    private final BotConfigService botConfigService;

    public BotConfigController(BotConfigService botConfigService) {
        this.botConfigService = botConfigService;
    }

    @GetMapping("/bot")
    public Map<String, Object> getBotConfig() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("config", botConfigService.getConfig());
        return res;
    }

    @PutMapping("/bot")
    public Map<String, Object> updateBotConfig(@RequestBody UpdateRequest req) {
        BotConfig updates = new BotConfig();
        updates.setAppId(req.getAppId());
        updates.setToken(req.getToken());
        updates.setChannelType(req.getChannelType());
        updates.setAiType(req.getAiType());
        updates.setModel(req.getModel());
        updates.setDashscopeApiKey(req.getDashscopeApiKey());
        updates.setDebug(req.getDebug());
        updates.setBaseUrl(req.getBaseUrl());
        updates.setCallbackUrl(req.getCallbackUrl());
        updates.setDownloadUrl(req.getDownloadUrl());
        updates.setGroupChatPrefix(req.getGroupChatPrefix());
        updates.setGroupNameWhiteList(req.getGroupNameWhiteList());
        updates.setImageRecognition(req.getImageRecognition());
        updates.setSingleChatPrefix(req.getSingleChatPrefix());
        updates.setImageCreatePrefix(req.getImageCreatePrefix());
        updates.setSingleChatReplyPrefix(req.getSingleChatReplyPrefix());
        updates.setSpeechRecognition(req.getSpeechRecognition());
        updates.setTextToVoice(req.getTextToVoice());
        updates.setVoiceReplyVoice(req.getVoiceReplyVoice());
        updates.setVoiceToText(req.getVoiceToText());
        updates.setTextToVoiceModel(req.getTextToVoiceModel());
        updates.setTtsVoiceId(req.getTtsVoiceId());
        updates.setSystemPrompt(req.getSystemPrompt());
        updates.setLocalhostIp(req.getLocalhostIp());

        BotConfig saved = botConfigService.updateConfig(updates);
        return Map.of("success", true, "config", saved);
    }

    @Data
    public static class UpdateRequest {
        private String appId;
        private String token;
        private String channelType;
        private String aiType;
        private String model;
        private String dashscopeApiKey;
        private Boolean debug;
        private String baseUrl;
        private String callbackUrl;
        private String downloadUrl;
        private java.util.List<String> groupChatPrefix;
        private java.util.List<String> groupNameWhiteList;
        private Boolean imageRecognition;
        private java.util.List<String> singleChatPrefix;
        private java.util.List<String> imageCreatePrefix;
        private String singleChatReplyPrefix;
        private Boolean speechRecognition;
        private String textToVoice;
        private Boolean voiceReplyVoice;
        private String voiceToText;
        private String textToVoiceModel;
        private String ttsVoiceId;
        private String systemPrompt;
        private String localhostIp;
    }
}
