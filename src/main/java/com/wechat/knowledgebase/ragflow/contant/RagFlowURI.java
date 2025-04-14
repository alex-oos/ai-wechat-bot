package com.wechat.knowledgebase.ragflow.contant;

/**
 * @author Alex
 * @since 2025/4/14 17:31
 * <p>
 * 所有的接口：
 * </p>
 */

public interface RagFlowURI {

    //Create session with chat assistant
    String CREATE_SESSION_URI = "/api/v1/chats/{chat_id}/sessions";

    //Converse with chat assistant
    String CHAT_ASSISTANT_URI = "/api/v1/chats/{chat_id}/completions";

}
