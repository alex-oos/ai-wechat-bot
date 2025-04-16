package com.wechat.knowledgebase.ragflow.service;

import com.wechat.ai.session.Session;
import com.wechat.knowledgebase.ragflow.entity.ReplyEntity;

/**
 * @author Alex
 * @since 2025/4/14 18:03
 * <p></p>
 */
public interface RagFlowService {

    ReplyEntity chatWithAssistant(String content, String sessionId);

    String createSession(String sessionId);
    String createRagflowChat(Session session);

}
