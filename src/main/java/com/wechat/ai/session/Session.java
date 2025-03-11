package com.wechat.ai.session;

import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.wechat.util.FileUtil;
import lombok.*;

import java.util.*;


/**
 * @author Alex
 * @since 2025/3/11 20:56
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Session {


    private String sessionId;

    private String systemPrompt;

    private List<Message> messages;


    public Session(String sessionId, String systemPrompt) {

        this.sessionId = sessionId;
        if (systemPrompt == null) {
            systemPrompt = Objects.requireNonNull(FileUtil.readFile()).getSystemPrompt();
        }
        this.systemPrompt = systemPrompt;
        this.messages = new ArrayList<>();
        initializeSystemMessage();
    }

    private void initializeSystemMessage() {


        Message sysMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        messages.add(sysMsg);
    }

    public void reset() {

        this.messages.clear();
        Message sysMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        this.messages.add(sysMsg);
    }

    public void setSystemPrompt(String systemPrompt) {

        this.systemPrompt = systemPrompt;
        reset();
    }

    public void addQuery(String query) {

        Message userMsg = Message.builder().role(Role.USER.getValue()).content(query).build();
        this.messages.add(userMsg);
    }

    public void addReply(String reply) {

        Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content(reply).build();

        this.messages.add(assistantMsg);
    }

    public void discardExceeding(Integer maxTokens, Integer currentTokens) {

        throw new UnsupportedOperationException("Not implemented");
    }

    public int calcTokens() {

        throw new UnsupportedOperationException("Not implemented");
    }

    public List<Message> getMessages() {

        return new ArrayList<>(messages);
    }

}
