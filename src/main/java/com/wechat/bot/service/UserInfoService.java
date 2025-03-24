package com.wechat.bot.service;

import com.wechat.bot.entity.ChatMessage;

import java.util.Map;

/**
 * @author Alex
 * @since 2025/3/24 17:20
 * <p></p>
 */
public interface UserInfoService {

    Map<String, String> getUserInfo();

    void updateUserInfo(String userId);

    void updateUserInfo(ChatMessage chatMessage);

}
