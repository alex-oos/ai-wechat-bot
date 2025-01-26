package com.wechat.bot.bot.service;

/**
 * @author Alex
 * @since 2025/1/26 19:59
 * <p></p>
 */
public interface CallBackService {


    Boolean filterUser(String fromUsername, String toUserName, String msgSource, String content);

}
