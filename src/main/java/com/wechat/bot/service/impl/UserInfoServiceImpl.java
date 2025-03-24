package com.wechat.bot.service.impl;

import com.wechat.bot.service.UserInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alex
 * @since 2025/3/24 17:20
 * <p></p>
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    /**
     * 联系人map，用线程安全的map
     */
    private final Map<String, String> userInfo = new ConcurrentHashMap<>();

    @Override
    public Map<String, String> getUserInfo() {

        return this.userInfo;
    }

}
