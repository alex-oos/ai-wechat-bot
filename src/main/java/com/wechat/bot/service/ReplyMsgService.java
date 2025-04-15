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


    //void replyTextMsg(ChatMessage chatMessage);

    void replyTextMsg(ChatMessage chatMessage);

    void replayMessage(ChatMessage chatMessage);

    void replyImageMsg(ChatMessage chatMessage);

    void replyVideoMsg(ChatMessage chatMessage);

    void replyFileMsg(ChatMessage chatMessage);

    void replyAudioMsg(ChatMessage chatMessage);

    void replyLocationMsg(ChatMessage chatMessage);

    void replyLinkMsg(ChatMessage chatMessage);

    void imageRecognition(ChatMessage chatMessage);

    /**
     * 回复类型是引用类型的消息，一般个人回复，不使用，群聊消息才使用
     *
     * @param content
     * @param referMsgId
     * @return
     */
    void replayQuoteMsg(ChatMessage chatMessage);

    /**
     * 回复的类型是艾特的信息，仅仅支持群聊才可以
     *
     * @param chatMessage
     */
    void replayAitMsg(ChatMessage chatMessage);

    void sendTextMessage(ChatMessage chatMessage);

}
