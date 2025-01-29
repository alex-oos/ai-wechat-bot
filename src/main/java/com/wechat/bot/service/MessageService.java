package com.wechat.bot.service;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.service.AIService;
import com.wechat.bot.entity.message.reply.ReplyTextMessage;

/**
 * @author Alex
 * @since 2025/1/26 19:59
 * <p></p>
 */
public interface MessageService {


    Boolean filterNotUserMessage(String wxid, String fromUsername, String toUserName, String msgSource, String content);

    /**
     * 过滤错误消息
     *
     * @param requestBody
     * @return
     */
    Boolean filterErrorMessage(String requestBody);

    void replyTextMsg(String receiveMsg, ReplyTextMessage replyTextMessage);

    void receiveMsg(JSONObject requestBody);

    AIService chooseAiService();

    void groupMsg(JSONObject requestBody);

    void sendMsgType(Integer msgType, String receiveMsg, String appid, String toUserName, String fromUserName,Boolean isGroup);

    void personalMsg(String receiveMsg, ReplyTextMessage replyTextMessage);

}
