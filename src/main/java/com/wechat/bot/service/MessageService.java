package com.wechat.bot.service;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.entity.ChatMessage;

/**
 * @author Alex
 * @since 2025/1/26 19:59
 * <p></p>
 */
public interface MessageService {

    /**
     * 过滤掉非用户消息
     * @param chatMessage
     * @param msgSource
     * @return
     */
    Boolean filterNotUserMessage(ChatMessage chatMessage, String msgSource);

    /**
     * 过滤错误消息
     *
     * @param requestBody
     * @return
     */
    Boolean filterErrorMessage(String requestBody);

    /**
     * 接收消息
     *
     * @param requestBody
     */

    void receiveMsg(JSONObject requestBody);



}
