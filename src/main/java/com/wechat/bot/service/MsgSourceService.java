package com.wechat.bot.service;

import com.wechat.bot.entity.ChatMessage;

/**
 * @author Alex
 * @since 2025/2/19 09:48
 * <p></p>
 */
public interface MsgSourceService {


    void personalMsg(ChatMessage chatMessage);

    void groupMsg(ChatMessage chatMessage);


}
