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

        Session session = sessionManager.getSession(chatMessage.getFromUserId());
        if (session == null) {
            if (!prefixFilter(chatMessage, botconfig.getSingleChatPrefix())) {
                return;
            }
            session = sessionManager.createSession(chatMessage.getFromUserId(), botconfig.getSystemPrompt());
        }
        switch (chatMessage.getCtype()) {
            case TEXT:
                handleTextMessage(chatMessage, session);
                break;
            case IMAGERECOGNITION:
                handleImageRecognitionMessage(chatMessage, session);
                if (session.getImageMessages().size() != 1) {
                    return;
                }
                break;
            case IMAGE:
                handleImageMessage(chatMessage, session);
                break;
            default:
                return;
        }
        if (session == null) {
            return;
        }
        session.setCreateTime(Instant.now());
        replyMsgService.replyType(chatMessage, session);
    }

    private void handleTextMessage(ChatMessage chatMessage, Session session) {

        String content = chatMessage.getContent();
        if (isClearMemoryCommand(content)) {
            clearSessionAndReply(chatMessage);
            return;
        }
        handleImageMessages(chatMessage, session);
    }

    private void handleImageRecognitionMessage(ChatMessage chatMessage, Session session) {

        if (session == null) {
            session = sessionManager.createSession(chatMessage.getFromUserId(), botconfig.getSystemPrompt());
        }
        adjustImagePath(chatMessage);
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Collections.singletonMap("image", "file://" + chatMessage.getContent()));
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(list).build();
        session.getImageMessages().add(userMessage);
    }

    private Session handleImageMessage(ChatMessage chatMessage, Session session) {

        session.addQuery(chatMessage.getContent());
        return session;
    }

    private void handleImageMessages(ChatMessage chatMessage, Session session) {

        List<MultiModalMessage> imageMessages = session.getImageMessages();
        if (imageMessages.size() == 1) {
            session.addQuery(chatMessage.getContent());
        }
        List<MultiModalMessage> multiModalMessages = imageMessages.stream()
                .filter(e -> e.getRole().equals(Role.USER.getValue()))
                .collect(Collectors.toList());
        for (MultiModalMessage imageMessage : multiModalMessages) {
            List<Map<String, Object>> contentList = imageMessage.getContent();
            long count = contentList.stream().filter(e -> e.get("text") != null).count();
            if (count == 0) {
                contentList.add(Collections.singletonMap("text", chatMessage.getContent()));
            } else {
                MultiModalMessage userMsg = MultiModalMessage.builder().role(Role.USER.getValue())
                        .content(List.of(Collections.singletonMap("text", chatMessage.getContent()))).build();
                imageMessages.add(userMsg);
            }
            chatMessage.setCtype(MsgTypeEnum.IMAGERECOGNITION);
        }
    }

    private boolean isClearMemoryCommand(String content) {

        return content.equals("#清除记忆") || content.equals("#退出") || content.equals("#清除") || content.equals("#清除记忆并退出") || content.equals("#人工");
    }

    private void clearSessionAndReply(ChatMessage chatMessage) {

        sessionManager.deleteSession(chatMessage.getFromUserId());
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), "恢复真人模式", chatMessage.getToUserId());
    }

    private boolean prefixFilter(ChatMessage chatMessage, List<String> prefixes) {

        if (prefixes.isEmpty()) {
            log.error("聊天前缀过滤失败,直接返回");
            return false;
        }
        long count = prefixes.stream().filter(e -> chatMessage.getContent().contains(e)).count();
        return count != 0;
    }

    private void adjustImagePath(ChatMessage chatMessage) {

        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().startsWith("win")) {
            chatMessage.setContent("/" + chatMessage.getContent().replace('\\', '/'));
        }
    }

    /**
     * 群消息，如何回复
     */
    @Override
    public void groupMsg(ChatMessage chatMessage) {

        String groupIdAndUserId = chatMessage.getGroupId() + "-" + chatMessage.getFromUserId();
        Session session = sessionManager.getSession(groupIdAndUserId);
        if (session == null) {
            if (!groupNameFilter(chatMessage) || !prefixFilter(chatMessage, botconfig.getGroupChatPrefix())) {
                return;
            }
            session = sessionManager.createSession(groupIdAndUserId, botconfig.getSystemPrompt());
        }
        switch (chatMessage.getCtype()) {
            case TEXT:
                handleTextMessage(chatMessage, session);
                break;
            case IMAGERECOGNITION:
                handleImageRecognitionMessage(chatMessage, session);
                if (session.getImageMessages().size() != 1) {
                    return;
                }
                break;
            case IMAGE:
                handleImageMessage(chatMessage, session);
                break;
        }
        if (session == null) {
            return;
        }
        session.setCreateTime(Instant.now());
        replyMsgService.replyType(chatMessage, session);

    }

    private boolean groupNameFilter(ChatMessage chatMessage) {

        List<String> groupNameWhiteList = botconfig.getGroupNameWhiteList();
        if (groupNameWhiteList.isEmpty()) {
            return false;
        }
        return groupNameWhiteList.get(0).equals("ALL_GROUP") || groupNameWhiteList.contains(chatMessage.getGroupIdNickName());
    }

    @Override
    public SessionManager getSessionManager() {

        return this.sessionManager;
    }

}
