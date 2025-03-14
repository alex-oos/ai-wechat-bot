package com.wechat.bot.service.impl;

import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.wechat.ai.session.Session;
import com.wechat.ai.session.SessionManager;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.MsgSourceService;
import com.wechat.bot.service.ReplyMsgService;
import com.wechat.gewechat.service.MessageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alex
 * @since 2025/2/19 09:49
 * <p></p>
 */
@Slf4j
@Service
public class MsgSourceServiceImpl implements MsgSourceService {

    private final SessionManager sessionManager = new SessionManager();

    @Resource
    BotConfig botconfig;

    @Resource
    private ReplyMsgService replyMsgService;

    /**
     * 个人消息
     */
    @Override
    public void personalMsg(ChatMessage chatMessage) {

        // 第一次 触发逻辑，判断，会话管理里面是否有消息，没有就创建会话
        // 第二次进行，直接取消息，然后进行回复
        Session session = sessionManager.getSession(chatMessage.getFromUserId());
        if (chatMessage.getCtype().equals(MsgTypeEnum.TEXT)) {
            // 会话清理的逻辑
            String content = chatMessage.getContent();
            if (content.equals("#清除记忆") || content.equals("#退出") || content.equals("#清除") || content.equals("#清除记忆并退出") || content.equals("#人工")) {
                sessionManager.deleteSession(chatMessage.getFromUserId());
                // 直接回复，清除记忆成功
                MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), "恢复真人模式", chatMessage.getToUserId());
                return;
            }
            if (session == null) {
                // 聊天前缀过滤
                List<String> singleChatPrefix = botconfig.getSingleChatPrefix();
                if (singleChatPrefix.isEmpty()) {
                    log.error("聊天前缀过滤失败,直接返回");
                    return;
                } else {
                    // 单独聊天前缀过滤
                    for (String chatPrefix : singleChatPrefix) {
                        if (!chatMessage.getContent().contains(chatPrefix)) {
                            return;
                        }
                    }
                    // 创建一个新的会话
                    sessionManager.createSession(chatMessage.getFromUserId(), botconfig.getSystemPrompt());
                    session = sessionManager.getSession(chatMessage.getFromUserId());

                }
            }

            // 先去追溯一下，是否有图片消息，如果有的话， 修改类型，进行一些列操作，如果没有的话，添加文本类型
            List<MultiModalMessage> imageMessages = session.getImageMessages();
            if (imageMessages.size() == 1) {
                session.addQuery(chatMessage.getContent());
            }
            //  添加到图片消息里面
            List<MultiModalMessage> multiModalMessages = imageMessages.stream().filter(e -> e.getRole().equals(Role.USER.getValue())).collect(Collectors.toList());
            for (MultiModalMessage imageMessage : multiModalMessages) {
                List<Map<String, Object>> contentList = imageMessage.getContent();
                long count = contentList.stream().filter(e -> e.get("text") != null).count();
                if (count == 0) {
                    List<Map<String, Object>> imageMessageContent = imageMessage.getContent();
                    imageMessageContent.add(Collections.singletonMap("text", chatMessage.getContent()));
                } else {
                    MultiModalMessage userMsg = MultiModalMessage.builder().role(Role.USER.getValue())
                            .content(List.of(Collections.singletonMap("text", chatMessage.getContent()))).build();
                    imageMessages.add(userMsg);
                }
                chatMessage.setCtype(MsgTypeEnum.IMAGERECOGNITION);

            }

        } else if (chatMessage.getCtype().equals(MsgTypeEnum.IMAGERECOGNITION)) {
            if (session == null) {
                // 创建一个新的会话
                sessionManager.createSession(chatMessage.getFromUserId(), botconfig.getSystemPrompt());
                session = sessionManager.getSession(chatMessage.getFromUserId());
            }
            // 不同的操作系统，需要区分一下路径
            // Linux或macOS系统   file://{文件的绝对路径} file:///home/images/test.png
            // Windows系统 file:///{文件的绝对路径} file:///D:images/test.png
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(Collections.singletonMap("image", "file://" + chatMessage.getContent()));
            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(list).build();
            session.getImageMessages().add(userMessage);
            return;
        }else{
            return;
        }
        //还有很多类型没有处理，等后续有空处理
        //// 设置一个session,防止为空，报错，
        //if (session == null) {
        //    // 创建一个新的会话
        //    sessionManager.createSession(chatMessage.getFromUserId(), null);
        //    session = sessionManager.getSession(chatMessage.getFromUserId());
        //}
        session.setCreateTime(Instant.now());
        replyMsgService.replyType(chatMessage, session);


    }


    /**
     * 群消息，如何回复
     */

    @Override
    public void groupMsg(ChatMessage chatMessage) {
        // 拿到群里面的session
        // 群里面的存储方式，主键是群id，里面每个session 存储的每个用户的会话，避免串，避免被其他群占用
        Session session = sessionManager.getSession(chatMessage.getFromUserId());
        if (chatMessage.getCtype().equals(MsgTypeEnum.TEXT)) {
            // 会话清理的逻辑
            String content = chatMessage.getContent();
            if (content.equals("#清除记忆") || content.equals("#退出") || content.equals("#清除") || content.equals("#清除记忆并退出") || content.equals("#人工")) {
                sessionManager.deleteSession(chatMessage.getFromUserId());
                // 直接回复，清除记忆成功
                MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), "恢复真人模式", chatMessage.getToUserId());
                return;
            }
        }
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
            //replyMsgService.replyType(chatMessage);
            return;
        }


    }

    @Override
    public SessionManager getSessionMessage() {

        return this.sessionManager;
    }

}
