package com.wechat.bot.bot.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.bot.service.CallBackService;
import com.wechat.bot.gewechat.service.MessageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alex
 * @since 2025/1/26 20:00
 * <p></p>
 */
@Slf4j
@Service
public class CallBackServiceImpl implements CallBackService {

    @Override
    public Boolean filterUser(String fromUsername, String toUserName, String msgSource, String content) {
        /**
         *   """检查消息是否来自非用户账号（如公众号、腾讯游戏、微信团队等）
         *
         *         Args:
         *             msg_source: 消息的MsgSource字段内容
         *             from_user_id: 消息发送者的ID
         *
         *         Returns:
         *             bool: 如果是非用户消息返回True，否则返回False
         *
         *         Note:
         *             通过以下方式判断是否为非用户消息：
         *             1. 检查MsgSource中是否包含特定标签
         *             2. 检查发送者ID是否为特殊账号或以特定前缀开头
         */

        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, "Tencent-Games", "weixin");
        if (list1.contains(fromUsername) || fromUsername.startsWith("gh_")) {
            return true;
        }
        // 添加过滤标签
        List<String> list = new ArrayList<>();
        Collections.addAll(list, "<tips>3</tips>", "<bizmsgshowtype>", "</bizmsgshowtype>", "<bizmsgfromuser>", "</bizmsgfromuser>");
        if (list.contains(msgSource)) {
            return true;
        }
        log.info("收到消息：{}", content);
        // 过滤掉自己
        if (toUserName.equals(fromUsername)) {
            return true;
        }
        if (content.contains(toUserName) || content.contains(fromUsername)) {
            return true;
        }
        return false;


    }

}
