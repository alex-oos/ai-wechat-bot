package com.wechat.ai.service;

import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.session.Session;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Alex
 * @since 2025/1/27 12:16
 * <p>
 * 抽象AI父类，为其提供共同的字段，并且提供默认的实现，便于管理
 * </p>
 */
@Getter
@Slf4j
public abstract class AbstractAiService implements AIService {

    protected final AiEnum aiEnum;

    public AbstractAiService(AiEnum aiEnum) {

        this.aiEnum = aiEnum;
    }


    @Override
    public String textToText(Session session) {

        return null;
    }

    @Override
    public Map<String, String> textToImage(String content) {

        return null;
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
