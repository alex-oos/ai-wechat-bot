package com.wechat.bot.service;

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
    String getqr();

    void getToken();

    /**
     * 4、确认登陆
     *
     * @param uuid 取码返回的uuid
     */
    void checkQr(String uuid);

}
