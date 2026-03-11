package com.wechat.bot.service;

import com.wechat.bot.enums.MsgTypeEnum;

import java.util.List;

/**
 * @author Alex
 * @since 2025/4/9 10:13
 * <p></p>
 */
public interface MsgTypeManageService {


    void clear(String userId);

    void clear(List<String> userIds);

    void add(String userId, MsgTypeEnum msgTypeEnum);

    MsgTypeEnum get(String userId);

}
