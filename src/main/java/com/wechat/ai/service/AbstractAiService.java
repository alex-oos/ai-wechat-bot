package com.wechat.ai.service;

import com.wechat.ai.enums.AiEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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


}
