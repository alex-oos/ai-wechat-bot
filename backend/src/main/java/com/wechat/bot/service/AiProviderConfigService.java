package com.wechat.bot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.dto.AiProviderConfigDTO;
import com.wechat.bot.entity.dto.AiProviderDTO;
import com.wechat.bot.mapper.AiProviderConfigMapper;
import com.wechat.bot.mapper.AiProviderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AiProviderConfigService {

    private final AiProviderConfigMapper mapper;
    private final AiProviderMapper providerMapper;
    private final BotConfigService botConfigService;
    private volatile AiProviderConfigDTO cached;

    public AiProviderConfigService(AiProviderConfigMapper mapper, AiProviderMapper providerMapper, BotConfigService botConfigService) {
        this.mapper = mapper;
        this.providerMapper = providerMapper;
        this.botConfigService = botConfigService;
    }

    public synchronized AiProviderConfigDTO loadOrSeed() {
        if (cached != null) {
            return cached;
        }
        AiProviderConfigDTO existing = selectFirst();
        if (existing != null) {
            cached = existing;
            ensureProviderSeed(existing);
            return cached;
        }

        BotConfig botConfig = botConfigService.getConfig();
        AiProviderConfigDTO seed = AiProviderConfigDTO.builder()
                .aiType(botConfig.getAiType())
                .model(botConfig.getModel())
                .apiKey(botConfig.getDashscopeApiKey())
                .apiBaseUrl("")
                .activeProviderId(null)
                .build();
        mapper.insert(seed);
        cached = seed;

        // seed provider list from existing ai config
        AiProviderDTO provider = AiProviderDTO.builder()
                .name("default")
                .aiType(botConfig.getAiType())
                .model(botConfig.getModel())
                .apiBaseUrl("")
                .apiKey(botConfig.getDashscopeApiKey())
                .enabled(1)
                .build();
        providerMapper.insert(provider);
        seed.setActiveProviderId(provider.getId());
        mapper.updateById(seed);
        return cached;
    }

    private void ensureProviderSeed(AiProviderConfigDTO cfg) {
        Long count = providerMapper.selectCount(new LambdaQueryWrapper<>());
        if (count != null && count > 0) {
            return;
        }
        AiProviderDTO provider = AiProviderDTO.builder()
                .name("default")
                .aiType(cfg.getAiType())
                .model(cfg.getModel())
                .apiBaseUrl(cfg.getApiBaseUrl())
                .apiKey(cfg.getApiKey())
                .enabled(1)
                .build();
        providerMapper.insert(provider);
        if (cfg.getActiveProviderId() == null) {
            cfg.setActiveProviderId(provider.getId());
            mapper.updateById(cfg);
        }
    }

    public AiProviderConfigDTO getConfig() {
        return loadOrSeed();
    }

    public synchronized AiProviderConfigDTO updateConfig(AiProviderConfigDTO updates) {
        AiProviderConfigDTO current = loadOrSeed();

        if (updates.getAiType() != null) current.setAiType(updates.getAiType());
        if (updates.getModel() != null) current.setModel(updates.getModel());
        if (updates.getApiBaseUrl() != null) current.setApiBaseUrl(updates.getApiBaseUrl());
        if (updates.getApiKey() != null) current.setApiKey(updates.getApiKey());
        if (updates.getActiveProviderId() != null) current.setActiveProviderId(updates.getActiveProviderId());

        if (updates.getActiveProviderId() != null) {
            AiProviderDTO provider = providerMapper.selectById(updates.getActiveProviderId());
            if (provider != null) {
                if (provider.getAiType() != null) current.setAiType(provider.getAiType());
                if (provider.getModel() != null) current.setModel(provider.getModel());
                if (provider.getApiBaseUrl() != null) current.setApiBaseUrl(provider.getApiBaseUrl());
                if (provider.getApiKey() != null) current.setApiKey(provider.getApiKey());
            }
        }

        mapper.updateById(current);
        cached = current;
        syncBotConfig(current);
        return current;
    }

    private AiProviderConfigDTO selectFirst() {
        List<AiProviderConfigDTO> list = mapper.selectList(
                new LambdaQueryWrapper<AiProviderConfigDTO>().orderByAsc(AiProviderConfigDTO::getId)
        );
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private void syncBotConfig(AiProviderConfigDTO cfg) {
        BotConfig updates = new BotConfig();
        updates.setAiType(cfg.getAiType());
        updates.setModel(cfg.getModel());
        updates.setDashscopeApiKey(cfg.getApiKey());
        botConfigService.updateConfig(updates);
    }

    public List<AiProviderDTO> listProviders() {
        List<AiProviderDTO> list = providerMapper.selectList(
                new LambdaQueryWrapper<AiProviderDTO>().orderByAsc(AiProviderDTO::getId)
        );
        return list == null ? new ArrayList<>() : list;
    }

    public AiProviderDTO saveProvider(AiProviderDTO provider) {
        if (provider.getId() == null) {
            if (provider.getEnabled() == null) {
                provider.setEnabled(1);
            }
            providerMapper.insert(provider);
            return provider;
        }
        providerMapper.updateById(provider);
        return provider;
    }

    public void deleteProvider(Long id) {
        if (id == null) return;
        providerMapper.deleteById(id);
        AiProviderConfigDTO cfg = loadOrSeed();
        if (cfg.getActiveProviderId() != null && cfg.getActiveProviderId().equals(id)) {
            cfg.setActiveProviderId(null);
            mapper.updateById(cfg);
            cached = cfg;
        }
    }

    public Optional<AiProviderDTO> getActiveProvider() {
        AiProviderConfigDTO cfg = loadOrSeed();
        Long activeId = cfg.getActiveProviderId();
        if (activeId == null) {
            return Optional.empty();
        }
        AiProviderDTO provider = providerMapper.selectById(activeId);
        if (provider == null || (provider.getEnabled() != null && provider.getEnabled() == 0)) {
            return Optional.empty();
        }
        return Optional.of(provider);
    }
}
