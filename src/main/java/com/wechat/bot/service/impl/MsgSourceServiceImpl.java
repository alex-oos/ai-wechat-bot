package com.wechat.bot.service.impl;

import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.service.MsgSourceService;
import com.wechat.bot.service.ReplyMsgService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Alex
 * @since 2025/2/19 09:49
 * <p></p>
 */
@Service
public class MsgSourceServiceImpl implements MsgSourceService {

    @Resource
    BotConfig botconfig;

    @Resource
    private ReplyMsgService replyMsgService;

    /**
     * 个人消息
     */
    @Async
    @Override
    public void personalMsg(ChatMessage chatMessage) {
        // 聊天前缀过滤
        List<String> singleChatPrefix = botconfig.getSingleChatPrefix();
        if (!singleChatPrefix.isEmpty()) {
            // 单独聊天前缀过滤
            for (String chatPrefix : singleChatPrefix) {
                if (!chatMessage.getContent().startsWith(chatPrefix)) {
                    return;
                }
            }
        }

        replyMsgService.replyType(chatMessage);


    }


    /**
     * 群消息，如何回复
     */
    @Async
    @Override
    public void groupMsg(ChatMessage chatMessage) {

        // 黑名单过滤
        List<String> groupNameWhiteList = botconfig.getGroupNameWhiteList();
        if (!groupNameWhiteList.isEmpty()) {

            if (!groupNameWhiteList.get(0).equals("ALL_GROUP")) {
                // 判断群名是否在白名单中
                if (!groupNameWhiteList.contains(chatMessage.getToUserNickname())) {
                    return;
                }
            }
            // 开始发消息
            // 区分类型，先判断是否需要艾特
            List<String> groupChatPrefix = botconfig.getGroupChatPrefix();
            if (groupChatPrefix.isEmpty()) {
                return;
            }
            for (String chatPrefix : groupChatPrefix) {
                //如果包含ai, 则需要艾特
                //@bot 特殊校验一下
                if (chatPrefix.contains("@bot") && chatMessage.getIsAt()) {
                    // TODO @bot
                    if (chatMessage.getContent().contains(chatMessage.getSelfDisplayName())) {
                        //  消息发送
                        String replace = chatMessage.getContent().replace("@" + chatMessage.getSelfDisplayName(), "");
                        chatMessage.setContent(replace);
                        //this.replyTextMsg(chatMessage);

                    }
                }

                if (!chatMessage.getContent().contains(chatPrefix)) {
                    return;
                }
                // 消息发送
                String replace = chatMessage.getContent().replace(chatPrefix, "");
                chatMessage.setContent(replace);
                //this.replyTextMsg(chatMessage);

            }

            //TODO(群消息，如何回复)
            //消息如何拼接，是否需要艾特人，等等之类的，还有各种各样的欢迎语

            // 判断一下消息的类型
            replyMsgService.replyType(chatMessage);
            return;
        }


    }

}
