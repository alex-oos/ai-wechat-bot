package com.wechat.ai.service;

import com.wechat.ai.enums.AiEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Alex
 * @since 2025/1/27 12:16
 * <p></p>
 */
@Getter
@Slf4j
public abstract class AbstractAiService implements AIService {

    protected final AiEnum aiEnum;

    public AbstractAiService(AiEnum aiEnum) {

        this.aiEnum = aiEnum;
    }


    @Override
    public List<String> textToText(String content) {

        return List.of();
    }

    @Override
    public List<String> textToImage(String content) {

        return List.of();
    }

    @Override
    public String imageToText(String content) {

        return "";
    }

    @Override
    public String imageToImage(String content) {

        return "";
    }

    @Override
    public String imageToImage(String content, String style) {

        return "";
    }

    @Override
    public String imageToImage(String content, String style, String prompt) {

        return "";
    }

    @Override
    public String imageToImage(String content, String style, String prompt, String negativePrompt) {

        return "";
    }


}
