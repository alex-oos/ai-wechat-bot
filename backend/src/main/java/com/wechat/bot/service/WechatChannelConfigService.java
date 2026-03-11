package com.wechat.bot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.dto.WechatChannelConfigDTO;
import com.wechat.bot.mapper.WechatChannelConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WechatChannelConfigService {

    private final WechatChannelConfigMapper mapper;
    private final BotConfigService botConfigService;
    private volatile WechatChannelConfigDTO cached;

    public WechatChannelConfigService(WechatChannelConfigMapper mapper, BotConfigService botConfigService) {
        this.mapper = mapper;
        this.botConfigService = botConfigService;
    }

    public synchronized WechatChannelConfigDTO loadOrSeed() {
        if (cached != null) {
            return cached;
        }
        WechatChannelConfigDTO existing = selectFirst();
        if (existing != null) {
            cached = existing;
            return cached;
        }

        BotConfig botConfig = botConfigService.getConfig();
        WechatChannelConfigDTO seed = WechatChannelConfigDTO.builder()
                .channelType(botConfig.getChannelType())
                .baseUrl(botConfig.getBaseUrl())
                .callbackUrl(botConfig.getCallbackUrl())
                .downloadUrl(botConfig.getDownloadUrl())
                .appId(botConfig.getAppId())
                .token(botConfig.getToken())
                .build();
        mapper.insert(seed);
        cached = seed;
        return cached;
    }

    public WechatChannelConfigDTO getConfig() {
        return loadOrSeed();
    }

    public synchronized WechatChannelConfigDTO updateConfig(WechatChannelConfigDTO updates) {
        WechatChannelConfigDTO current = loadOrSeed();

        if (updates.getChannelType() != null) current.setChannelType(updates.getChannelType());
        if (updates.getBaseUrl() != null) current.setBaseUrl(updates.getBaseUrl());
        if (updates.getCallbackUrl() != null) current.setCallbackUrl(updates.getCallbackUrl());
        if (updates.getDownloadUrl() != null) current.setDownloadUrl(updates.getDownloadUrl());
        if (updates.getAppId() != null) current.setAppId(updates.getAppId());
        if (updates.getToken() != null) current.setToken(updates.getToken());

        mapper.updateById(current);
        cached = current;
        syncBotConfig(current);
        return current;
    }

    private WechatChannelConfigDTO selectFirst() {
        List<WechatChannelConfigDTO> list = mapper.selectList(
                new LambdaQueryWrapper<WechatChannelConfigDTO>().orderByAsc(WechatChannelConfigDTO::getId)
        );
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private void syncBotConfig(WechatChannelConfigDTO cfg) {
        BotConfig updates = new BotConfig();
        updates.setChannelType(cfg.getChannelType());
        updates.setBaseUrl(cfg.getBaseUrl());
        updates.setCallbackUrl(cfg.getCallbackUrl());
        updates.setDownloadUrl(cfg.getDownloadUrl());
        updates.setAppId(cfg.getAppId());
        updates.setToken(cfg.getToken());
        botConfigService.updateConfig(updates);
    }
}
