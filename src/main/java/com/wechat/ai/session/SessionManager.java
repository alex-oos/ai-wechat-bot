package com.wechat.ai.session;

/**
 * @author Alex
 * @since 2025/3/11 15:38
 * <p></p>
 */

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private static final int MAX_TOKENS = 4096;

    public String createSession(String systemPrompt) {

        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId, systemPrompt);
        sessions.put(sessionId, session);
        return sessionId;
    }

    public void updateSession(String sessionId, List<Message> messages) {

        Session session = sessions.get(sessionId);
        if (session != null) {
            session.getMessages().addAll(messages);
            int currentTokens = calculateTokens(session.getMessages());
            if (currentTokens > MAX_TOKENS) {
                trimSession(session, currentTokens);
            }
        }
    }

    private void trimSession(Session session, int currentTokens) {

        List<Message> messages = session.getMessages();
        while (currentTokens > MAX_TOKENS && messages.size() > 2) {
            messages.remove(1);
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

    public Session getSession(String sessionId) {

        return sessions.get(sessionId);
    }

    public void deleteSession(String sessionId) {

        sessions.remove(sessionId);
    }

}

@Getter
class Session {

    // Getters and Setters

    private final String sessionId;

    private final String systemPrompt;

    private final List<Message> messages;

    private final String model = "qianwen";

    public Session(String sessionId, String systemPrompt) {

        this.sessionId = sessionId;
        this.systemPrompt = systemPrompt;
        this.messages = new ArrayList<>();
        initializeSystemMessage();
    }

    private void initializeSystemMessage() {

        messages.add(new Message("system", systemPrompt));
    }

}

class Message {

    private final String role;

    private final String content;

    public Message(String role, String content) {

        this.role = role;
        this.content = content;
    }

    // Getters
    public String getRole() {

        return role;
    }

    public String getContent() {

        return content;
    }

}
