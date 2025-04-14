package com.wechat.knowledgebase.ragflow.service;

/**
 * @author Alex
 * @since 2025/4/14 18:03
 * <p></p>
 */
public interface ChatService {

    String chatWithAssistant(String content, String sessionId);

    String createSession(String sessionId);

}
