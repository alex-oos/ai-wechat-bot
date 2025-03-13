package com.wechat.ai.session;

import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.wechat.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * @author Alex
 * @since 2025/3/11 20:56
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Session {

    private Instant createTime;

    private String sessionId;

    private String systemPrompt;

    /**
     * 文本对话信息
     */
    private List<Message> textMessages;

    /**
     * 图片对话信息
     */
    private List<MultiModalMessage> imageMessages;

    public Session(String sessionId, String systemPrompt) {

        this.sessionId = sessionId;
        if (systemPrompt == null) {
            systemPrompt = Objects.requireNonNull(FileUtil.readFile()).getSystemPrompt();
        }
        this.createTime = Instant.now();
        this.systemPrompt = systemPrompt;
        this.textMessages = new ArrayList<>();
        this.imageMessages = new ArrayList<>();
        initializeSystemMessage();
    }

    private void initializeSystemMessage() {


        Message sysMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        textMessages.add(sysMsg);

        MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                .content(List.of(Collections.singletonMap("text", systemPrompt))).build();
        //MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
        //        .content(Arrays.asList(Collections.singletonMap("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg"), Collections.singletonMap("text", "图中描绘的是什么景象？"))).build();
        imageMessages.add(systemMessage);
    }

    public void reset() {

        this.textMessages.clear();
        Message sysMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        this.textMessages.add(sysMsg);
    }

    public void setSystemPrompt(String systemPrompt) {

        this.systemPrompt = systemPrompt;
        reset();
    }

    public void addQuery(String query) {

        Message userMsg = Message.builder().role(Role.USER.getValue()).content(query).build();
        this.textMessages.add(userMsg);
    }

    public void addReply(String reply) {

        Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content(reply).build();

        this.textMessages.add(assistantMsg);
    }

    public void discardExceeding(Integer maxTokens, Integer currentTokens) {

        throw new UnsupportedOperationException("Not implemented");
    }

    public int calcTokens() {

        throw new UnsupportedOperationException("Not implemented");
    }


}
