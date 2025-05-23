package com.wechat.ai.session;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.wechat.bot.enums.MsgTypeEnum;
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

    /**
     * 聊天类型
     */
    private MsgTypeEnum msgType;

    /**
     * 是否开启搜索功能
     */
    private Boolean isActiveSearch;

    /**
     * 创建时间
     */
    private Instant createTime;

    /**
     * userid
     */
    private String userId;

    /**
     * 提示词
     */
    private String systemPrompt;

    /**
     * 文本对话信息
     */
    private List<Message> textMessages;

    /**
     * 图片对话信息
     */
    private List<MultiModalMessage> imageMessages;

    /**
     * 多模态对话信息
     */
    private MultiModalConversationParam multiModalConversationParam;

    public Session(String sessionId, String systemPrompt) {

        this.userId = sessionId;
        if (systemPrompt == null) {
            systemPrompt = Objects.requireNonNull(FileUtil.readFile()).getSystemPrompt();
        }
        this.createTime = Instant.now();
        this.systemPrompt = systemPrompt;
        this.textMessages = new ArrayList<>();
        this.imageMessages = new ArrayList<>();
        this.isActiveSearch = false;
        initializeSystemMessage();
    }

    private void initializeSystemMessage() {


        Message sysMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        textMessages.add(sysMsg);

        MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                .content(List.of(Collections.singletonMap("text", "你是一个图片识别助手，请根据图片描述，输出图片的描述信息"))).build();
        imageMessages.add(systemMessage);
    }

    public void setSystemPrompt(String systemPrompt) {

        this.systemPrompt = systemPrompt;
        reset();
    }

    public void reset() {

        this.textMessages.clear();
        Message sysMsg = Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build();
        this.textMessages.add(sysMsg);
    }

    public void addQuery(String queryText) {

        if (queryText != null) {
            Message userMsg = Message.builder().role(Role.USER.getValue()).content(queryText).build();
            this.textMessages.add(userMsg);
        }


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
