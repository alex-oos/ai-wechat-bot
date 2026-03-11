package com.wechat.ai.openai.service;

import com.alibaba.dashscope.common.Message;
import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.dto.AiProviderConfigDTO;
import com.wechat.bot.entity.dto.AiProviderDTO;
import com.wechat.bot.service.AiProviderConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OpenAiCompatService extends AbstractAiService {

    private static final String DEFAULT_OPENAI_BASE_URL = "https://api.openai.com/v1";

    private final AiProviderConfigService aiProviderConfigService;

    public OpenAiCompatService(AiProviderConfigService aiProviderConfigService) {
        super(AiEnum.OPENAI_COMPAT);
        this.aiProviderConfigService = aiProviderConfigService;
    }

    @Override
    public List<String> supportedAiTypes() {
        return List.of("openai", "qwen", "zhipu", "gemini", "deepseek", "openai-compat");
    }

    @Override
    public String textToText(Session session) {
        if (session == null) {
            throw new RuntimeException("session不能为空");
        }
        AiProviderConfigDTO cfg = aiProviderConfigService.getConfig();
        AiProviderDTO active = aiProviderConfigService.getActiveProvider().orElse(null);

        String apiKey = active != null ? active.getApiKey() : cfg.getApiKey();
        String model = active != null ? active.getModel() : cfg.getModel();
        String baseUrl = active != null ? active.getApiBaseUrl() : cfg.getApiBaseUrl();
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("AI API Key 未配置");
        }
        if (model == null || model.isBlank()) {
            throw new RuntimeException("AI 模型未配置");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = DEFAULT_OPENAI_BASE_URL;
        }

        OpenAiApi api = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .build();

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .build();

        Prompt prompt = new Prompt(toSpringMessages(session.getTextMessages()));
        String reply = chatModel.call(prompt).getResult().getOutput().getText();
        session.addReply(reply);
        return reply;
    }

    @Override
    public java.util.Map<String, String> textToImage(String content) {
        throw new UnsupportedOperationException("OpenAI兼容模式未实现文生图");
    }

    @Override
    public String imageToText(Session session) {
        throw new UnsupportedOperationException("OpenAI兼容模式未实现图像识别");
    }

    @Override
    public java.util.Map<String, Object> textToVideo(String content) {
        throw new UnsupportedOperationException("OpenAI兼容模式未实现文生视频");
    }

    @Override
    public Integer textToVoice(String content, String audioPath) {
        throw new UnsupportedOperationException("OpenAI兼容模式未实现语音合成");
    }

    private List<org.springframework.ai.chat.messages.Message> toSpringMessages(List<Message> messages) {
        List<org.springframework.ai.chat.messages.Message> mapped = new ArrayList<>();
        if (messages == null) {
            return mapped;
        }
        for (Message msg : messages) {
            if (msg == null) {
                continue;
            }
            String role = msg.getRole();
            String content = msg.getContent();
            if ("system".equalsIgnoreCase(role)) {
                mapped.add(new SystemMessage(content));
            } else if ("assistant".equalsIgnoreCase(role)) {
                mapped.add(new AssistantMessage(content));
            } else {
                mapped.add(new UserMessage(content));
            }
        }
        return mapped;
    }
}
