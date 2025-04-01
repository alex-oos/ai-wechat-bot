package com.wechat.bot.entity;

import com.wechat.bot.enums.MsgTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author Alex
 * @since 2025/1/29 22:07
 * <p></p>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatMessage {


    /**
     * 消息id
     */
    private String msgId;

    /**
     * 消息创建时间
     */
    private Long createTime;

    /**
     * 消息类型
     */
    private MsgTypeEnum ctype;

    /**
     * 消息内容
     */
    private String content;

    // 个人聊天使用

    private Boolean isMyMsg;

    /**
     * 消息发送者id
     */
    private String fromUserId;

    /**
     * 消息发送者昵称
     */
    private String fromUserNickname;

    /**
     * 消息接收者id
     */
    private String toUserId;

    /**
     * 消息接收者昵称
     */
    private String toUserNickname;


    // 群聊使用

    /**
     * 是否是群聊
     */
    private Boolean isGroup;

    /**
     * 群id
     */
    private String groupId;

    /**
     * 群昵称
     */
    private String groupIdNickName;

    /**
     * 是否被@
     */
    private Boolean isAt;

    /**
     * 群成员用户id
     */
    private String groupMembersUserId;

    /**
     * 群成员昵称
     */
    private String groupMemberUserNickname;

    /**
     * 被@的用户id
     */
    private List<String> atList;

    /**
     * 消息预处理函数
     */
    private Runnable prepareFn;

    /**
     * 消息是否已经处理过了
     */
    private Boolean prepared;

    /**
     * 原始消息
     */
    private Object rawMsg;

    /**
     * 设备id
     */
    private String appId;

    private String replayContent;


    public void prepare() {

        if (prepareFn != null && !prepared) {
            prepared = true;
            prepareFn.run();
        }
    }

    @Override
    public String toString() {

        return "ChatMessage: " +
                "id=" + msgId +
                ", create_time=" + createTime +
                ", ctype=" + ctype +
                ", content=" + content +
                ", from_user_id=" + fromUserId +
                ", from_user_nickname=" + fromUserNickname +
                ", to_user_id=" + toUserId +
                ", to_user_nickname=" + toUserNickname +
                ", other_user_id=" + groupId +
                ", other_user_nickname=" + groupIdNickName +
                ", is_group=" + isGroup +
                ", is_at=" + isAt +
                ", actual_user_id=" + groupMembersUserId +
                ", actual_user_nickname=" + groupMemberUserNickname +
                ", at_list=" + (atList != null ? String.join(", ", atList) : "null");
    }

}
