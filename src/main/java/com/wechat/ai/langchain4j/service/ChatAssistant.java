package com.wechat.ai.langchain4j.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

/**
 * @author Alex
 * @since 2025/4/22 17:04
 * <p></p>
 */
@Aiservice
public interface ChatAssistant {

    /**
     * 聊天
     *
     * @param message 消息
     * @return {@link String }
     */
    String chat(String message);


    /**
     * 聊天
     *
     * @param userId  用户 ID  (根据ID隔离记忆)
     * @param message 消息
     * @return {@link String }
     */
    String chat(@MemoryId Long userId, @UserMessage String message);

}
