package com.wechat.bot.service;

import java.util.Map;

/**
 * @author Alex
 * @since 2025/1/27 12:01
 * <p></p>
 */
public interface LoginService {

    void login();

    void setCallbackUrl();

    void getALLFriends();

    /**
     * 3、 获取登录二维码
     */
    Map<String, String> getqr(String appId);

    void getToken();

    /**
     * 4、确认登陆
     *
     * @param uuid 取码返回的uuid
     */
    Boolean checkStatus(Map<String, String> map);

}
