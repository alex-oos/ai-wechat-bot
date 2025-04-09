package com.wechat.bot.service.impl;

import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.MsgTypeManageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alex
 * @since 2025/4/9 10:13
 * <p>
 * 消息类型管理
 * </p>
 */
@Service
public class MsgTypeManageServiceImpl implements MsgTypeManageService {

    private final Map<String, MsgTypeEnum> msgTypeEnumMap = new ConcurrentHashMap<>();

    @Override
    public void clear(String userid) {

        msgTypeEnumMap.remove(userid);
    }

    @Override
    public void clear(List<String> userIds) {

        userIds.forEach(msgTypeEnumMap::remove);

    }

    @Override
    public void add(String userId, MsgTypeEnum msgTypeEnum) {

        msgTypeEnumMap.put(userId, msgTypeEnum);
    }

    @Override
    public MsgTypeEnum get(String userId) {

        return msgTypeEnumMap.getOrDefault(userId, MsgTypeEnum.TEXT);

    }

}
