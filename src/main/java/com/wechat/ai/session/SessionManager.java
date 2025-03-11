package com.wechat.ai.session;


import com.alibaba.dashscope.common.Message;

import java.util.List;
import java.util.Map;
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

    public String createSession(String userId, String systemPrompt) {

        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId, systemPrompt);
        sessions.put(userId, session);
        return sessionId;
    }

    public void updateSession(String userId, List<Message> messages) {

        Session session = sessions.get(userId);
        if (session != null) {
            session.getMessages().addAll(messages);
            int currentTokens = calculateTokens(session.getMessages());
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
            int currentTokens = calculateTokens(session.getMessages());
            if (currentTokens > MAX_TOKENS) {
                trimSession(session, currentTokens);
            }
        }
    }

    private void trimSession(Session session, int currentTokens) {

        List<Message> messages = session.getMessages();
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


}
