package com.wechat.bot.service;

import com.wechat.ai.service.AIService;
import com.wechat.bot.entity.ChatMessage;

/**
 * @author Alex
 * @since 2025/2/18 21:36
 * <p></p>
 */
public interface ReplyMsgService {

    AIService chooseAiService();


    void replyTextMsg(ChatMessage chatMessage);


    void replyType(ChatMessage chatMessage);

    void replyImageMsg(ChatMessage chatMessage);

    void replyVideoMsg(ChatMessage chatMessage);

    void replyFileMsg(ChatMessage chatMessage);

    void replyAudioMsg(ChatMessage chatMessage);

    void replyLocationMsg(ChatMessage chatMessage);

    void replyLinkMsg(ChatMessage chatMessage);

}
