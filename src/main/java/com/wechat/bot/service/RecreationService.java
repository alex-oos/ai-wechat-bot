package com.wechat.bot.service;

import com.wechat.bot.entity.ChatMessage;

/**
 * @author Alex
 * @since 2025/4/15 16:33
 * <p>娱乐功能</p>
 */
public interface RecreationService {

    /**
     * 天气预报
     *
     * @param chatMessage
     */
    void weatherReminder(ChatMessage chatMessage);

    /**
     * 早报
     *
     * @param chatMessage
     */
    void morning(ChatMessage chatMessage);

}
