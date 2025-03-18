package com.wechat.ai.session;


import com.alibaba.dashscope.common.Message;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alex
 * @since 2025/3/11 15:38
 * <p></p>
 */
public class SessionManager {


    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private static final int MAX_TOKENS = 8192;

    public Session createSession(String userId, String systemPrompt) {
        // 目录这里sessionid AI 暂时不使用，用userid存储，方便群消息管理
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(userId, systemPrompt);
        sessions.put(userId, session);
        return session;

    }

    public Session createGroupSession(String userId, String systemPrompt) {
        // 目录这里sessionid AI 暂时不使用，用userid存储，方便群消息管理
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(userId, systemPrompt);
        sessions.put(userId, session);
        return session;

    }

    public void updateSession(String userId, List<Message> messages) {

        Session session = sessions.get(userId);
        if (session != null) {
            session.getTextMessages().addAll(messages);
            int currentTokens = calculateTokens(session.getTextMessages());
            if (currentTokens > MAX_TOKENS) {
                trimSession(session, currentTokens);
            }
        }
    }

    public void addMessage(String userId, String query, String reply) {

        Session session = sessions.get(userId);
        if (session != null) {
            if (query != null) {
                session.addQuery(query);
            }
            if (reply != null) {
                session.addReply(reply);
            }
            int currentTokens = calculateTokens(session.getTextMessages());
            if (currentTokens > MAX_TOKENS) {
                trimSession(session, currentTokens);
            }
        }
    }

    private void trimSession(Session session, int currentTokens) {

        List<Message> messages = session.getTextMessages();
        while (currentTokens > MAX_TOKENS && messages.size() > 2) {
            messages.remove(0);
            currentTokens = calculateTokens(messages);
        }
    }

    private int calculateTokens(List<Message> messages) {

        int tokens = 0;
        for (Message msg : messages) {
            tokens += msg.getContent().length();
        }
        return tokens;
    }

    public Session getSession(String userId) {

        return sessions.get(userId);
    }


    public void deleteSession(String userId) {

        sessions.remove(userId);
    }

    /**
     * 自动清除过期会话
     */
    public void clearExpiredSessions() {

        Set<String> userSet = sessions.keySet();
        Instant now = Instant.now();
        for (String userId : userSet) {
            Session session = sessions.get(userId);
            Instant createTime = session.getCreateTime();
            // 当前时间是否在10分钟之内，如果超过5分钟，则删除会话
            if (now.isAfter(createTime.plusSeconds(60 * 5))) {
                sessions.remove(userId);
            }
        }
    }


}
