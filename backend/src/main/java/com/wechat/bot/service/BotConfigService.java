package com.wechat.bot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.ai.config.AiConfig;
import com.wechat.bot.config.BotConfigHolder;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.mapper.BotConfigMapper;
import com.wechat.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@Service
public class BotConfigService {

    private final BotConfigMapper botConfigMapper;
    private volatile BotConfig cached;

    public BotConfigService(BotConfigMapper botConfigMapper) {
        this.botConfigMapper = botConfigMapper;
    }

    public synchronized BotConfig loadOrSeed() {
        if (cached != null) {
            return cached;
        }

        BotConfig existing = selectFirst();
        if (existing != null) {
            normalize(existing);
            cached = existing;
            syncToStatic(cached);
            return cached;
        }

        BotConfig seeded = null;
        try {
            seeded = FileUtil.readFile();
        } catch (Exception ex) {
            log.warn("Failed to read config.json, using empty config.", ex);
            seeded = new BotConfig();
        }
        normalize(seeded);
        botConfigMapper.insert(seeded);
        cached = seeded;
        syncToStatic(cached);
        return cached;
    }

    public BotConfig getConfig() {
        return loadOrSeed();
    }

    public synchronized BotConfig updateConfig(BotConfig updates) {
        BotConfig current = loadOrSeed();

        if (updates.getAppId() != null) current.setAppId(updates.getAppId());
        if (updates.getToken() != null) current.setToken(updates.getToken());
        if (updates.getChannelType() != null) current.setChannelType(updates.getChannelType());
        if (updates.getAiType() != null) current.setAiType(updates.getAiType());
        if (updates.getModel() != null) current.setModel(updates.getModel());
        if (updates.getDashscopeApiKey() != null) current.setDashscopeApiKey(updates.getDashscopeApiKey());
        if (updates.getDebug() != null) current.setDebug(updates.getDebug());
        if (updates.getBaseUrl() != null) current.setBaseUrl(updates.getBaseUrl());
        if (updates.getCallbackUrl() != null) current.setCallbackUrl(updates.getCallbackUrl());
        if (updates.getDownloadUrl() != null) current.setDownloadUrl(updates.getDownloadUrl());
        if (updates.getGroupChatPrefix() != null) current.setGroupChatPrefix(updates.getGroupChatPrefix());
        if (updates.getGroupNameWhiteList() != null) current.setGroupNameWhiteList(updates.getGroupNameWhiteList());
        if (updates.getImageRecognition() != null) current.setImageRecognition(updates.getImageRecognition());
        if (updates.getSingleChatPrefix() != null) current.setSingleChatPrefix(updates.getSingleChatPrefix());
        if (updates.getImageCreatePrefix() != null) current.setImageCreatePrefix(updates.getImageCreatePrefix());
        if (updates.getSingleChatReplyPrefix() != null) current.setSingleChatReplyPrefix(updates.getSingleChatReplyPrefix());
        if (updates.getSpeechRecognition() != null) current.setSpeechRecognition(updates.getSpeechRecognition());
        if (updates.getTextToVoice() != null) current.setTextToVoice(updates.getTextToVoice());
        if (updates.getVoiceReplyVoice() != null) current.setVoiceReplyVoice(updates.getVoiceReplyVoice());
        if (updates.getVoiceToText() != null) current.setVoiceToText(updates.getVoiceToText());
        if (updates.getTextToVoiceModel() != null) current.setTextToVoiceModel(updates.getTextToVoiceModel());
        if (updates.getTtsVoiceId() != null) current.setTtsVoiceId(updates.getTtsVoiceId());
        if (updates.getSystemPrompt() != null) current.setSystemPrompt(updates.getSystemPrompt());
        if (updates.getLocalhostIp() != null) current.setLocalhostIp(updates.getLocalhostIp());

        normalize(current);
        botConfigMapper.updateById(current);
        syncToStatic(current);
        return current;
    }

    public synchronized BotConfig saveConfig(BotConfig config) {
        BotConfig current = loadOrSeed();
        config.setId(current.getId());
        botConfigMapper.updateById(config);
        normalize(config);
        cached = config;
        syncToStatic(config);
        return config;
    }

    private BotConfig selectFirst() {
        List<BotConfig> list = botConfigMapper.selectList(
                new LambdaQueryWrapper<BotConfig>().orderByAsc(BotConfig::getId)
        );
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private void syncToStatic(BotConfig config) {
        BotConfigHolder.set(config);
        AiConfig.setBotConfig(config);
    }

    private void normalize(BotConfig config) {
        if (config.getDebug() == null) config.setDebug(false);
        if (config.getImageRecognition() == null) config.setImageRecognition(false);
        if (config.getSpeechRecognition() == null) config.setSpeechRecognition(false);
        if (config.getVoiceReplyVoice() == null) config.setVoiceReplyVoice(false);
        if (config.getGroupChatPrefix() == null) config.setGroupChatPrefix(new ArrayList<>());
        if (config.getGroupNameWhiteList() == null) config.setGroupNameWhiteList(new ArrayList<>());
        if (config.getSingleChatPrefix() == null) config.setSingleChatPrefix(new ArrayList<>());
        if (config.getImageCreatePrefix() == null) config.setImageCreatePrefix(new ArrayList<>());
    }
}
