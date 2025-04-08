package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.service.UserInfoService;
import com.wechat.gewechat.service.ContactApi;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
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

    @Resource
    private BotConfig botConfig;

    @Override
    public Map<String, String> getUserInfo() {

        return this.userInfo;
    }

    @Override
    public void updateUserInfo(String userId) {

        if (!userInfo.containsKey(userId)) {
            // 存到一个map里面不用每次都重新获取，降低请求次数
            // 获取好友的信息
            String nickName = getNickname(userId);
            if (nickName != null) {
                userInfo.put(userId, nickName);
            }
        }


    }

    @Override
    public void updateUserInfo(ChatMessage chatMessage) {
        this.updateUserInfo(chatMessage.getFromUserId());
        this.updateUserInfo(chatMessage.getToUserId());
        chatMessage.setFromUserNickname(this.userInfo.get(chatMessage.getFromUserId()));
        chatMessage.setToUserNickname(this.userInfo.get(chatMessage.getToUserId()));


    }

    private String getNickname(String userId) {

        JSONObject briefInfo = ContactApi.getBriefInfo(botConfig.getAppId(), Collections.singletonList(userId));
        if (briefInfo.getInteger("ret") == 200) {
            JSONArray dataList = briefInfo.getJSONArray("data");
            if (dataList.size() > 0) {
                JSONObject userInfo = dataList.getJSONObject(0);
                String remark = userInfo.getString("remark");
                if (remark == null || remark.isBlank()) {
                    return userInfo.getString("nickName");
                }
                return remark;
            }
        }
        return null;
    }

}
