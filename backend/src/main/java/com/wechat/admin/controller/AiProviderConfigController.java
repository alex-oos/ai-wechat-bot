package com.wechat.admin.controller;

import com.wechat.bot.entity.dto.AiProviderConfigDTO;
import com.wechat.bot.entity.dto.AiProviderDTO;
import com.wechat.bot.service.AiProviderConfigService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config/ai")
public class AiProviderConfigController {

    private final AiProviderConfigService service;

    public AiProviderConfigController(AiProviderConfigService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> getConfig() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("config", service.getConfig());
        res.put("providers", service.listProviders());
        return res;
    }

    @PutMapping
    public Map<String, Object> updateConfig(@RequestBody UpdateRequest req) {
        AiProviderConfigDTO updates = new AiProviderConfigDTO();
        updates.setAiType(req.getAiType());
        updates.setModel(req.getModel());
        updates.setApiBaseUrl(req.getApiBaseUrl());
        updates.setApiKey(req.getApiKey());
        updates.setActiveProviderId(req.getActiveProviderId());
        AiProviderConfigDTO saved = service.updateConfig(updates);
        return Map.of("success", true, "config", saved, "providers", service.listProviders());
    }

    @PostMapping("/providers")
    public Map<String, Object> createProvider(@RequestBody ProviderRequest req) {
        AiProviderDTO provider = new AiProviderDTO();
        provider.setName(req.getName());
        provider.setAiType(req.getAiType());
        provider.setModel(req.getModel());
        provider.setApiBaseUrl(req.getApiBaseUrl());
        provider.setApiKey(req.getApiKey());
        provider.setEnabled(req.getEnabled());
        AiProviderDTO saved = service.saveProvider(provider);
        return Map.of("success", true, "provider", saved);
    }

    @PutMapping("/providers/{id}")
    public Map<String, Object> updateProvider(@PathVariable("id") Long id, @RequestBody ProviderRequest req) {
        AiProviderDTO provider = new AiProviderDTO();
        provider.setId(id);
        provider.setName(req.getName());
        provider.setAiType(req.getAiType());
        provider.setModel(req.getModel());
        provider.setApiBaseUrl(req.getApiBaseUrl());
        provider.setApiKey(req.getApiKey());
        provider.setEnabled(req.getEnabled());
        AiProviderDTO saved = service.saveProvider(provider);
        return Map.of("success", true, "provider", saved);
    }

    @DeleteMapping("/providers/{id}")
    public Map<String, Object> deleteProvider(@PathVariable("id") Long id) {
        service.deleteProvider(id);
        return Map.of("success", true);
    }

    @PutMapping("/active/{id}")
    public Map<String, Object> setActive(@PathVariable("id") Long id) {
        AiProviderConfigDTO updates = new AiProviderConfigDTO();
        updates.setActiveProviderId(id);
        AiProviderConfigDTO saved = service.updateConfig(updates);
        return Map.of("success", true, "config", saved);
    }

    @Data
    public static class UpdateRequest {
        private String aiType;
        private String model;
        private String apiBaseUrl;
        private String apiKey;
        private Long activeProviderId;
    }

    @Data
    public static class ProviderRequest {
        private String name;
        private String aiType;
        private String model;
        private String apiBaseUrl;
        private String apiKey;
        private Integer enabled;
    }
}
