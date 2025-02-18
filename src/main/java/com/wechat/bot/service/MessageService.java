package com.wechat.bot.service;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.service.AIService;
import com.wechat.bot.entity.ChatMessage;

/**
 * @author Alex
 * @since 2025/1/26 19:59
 * <p></p>
 */
public interface MessageService {


    Boolean filterNotUserMessage(ChatMessage chatMessage,String msgSource);

    /**
     * 过滤错误消息
     *
     * @param requestBody
     * @return
     */
    Boolean filterErrorMessage(String requestBody);


    void receiveMsg(JSONObject requestBody);

    void groupMsg(ChatMessage chatMessage);


    void personalMsg(ChatMessage chatMessage);

}
