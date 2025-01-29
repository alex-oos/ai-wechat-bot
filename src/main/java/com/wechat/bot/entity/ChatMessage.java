package com.wechat.bot.entity;

import com.wechat.bot.contant.MsgTypeEnum;
import lombok.*;

import java.util.ArrayList;
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

    private String msgId;

    private Long createTime;

    private MsgTypeEnum ctype;

    private String content;

    private String fromUserId;

    private String fromUserNickname;

    private String toUserId;

    private String toUserNickname;

    private String otherUserId;

    private String otherUserNickname;

    private Boolean isMyMsg;

    private String selfDisplayName;

    private Boolean isGroup;

    private Boolean isAt;

    private String actualUserId;

    private String actualUserNickname;

    private List<String>  atList;

    private Runnable prepareFn;

    private boolean prepared;

    /**
     * 原始消息
     */
    private Object rawMsg;

    /**
     * 设备id
     */
    private String appId;


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
                ", other_user_id=" + otherUserId +
                ", other_user_nickname=" + otherUserNickname +
                ", is_group=" + isGroup +
                ", is_at=" + isAt +
                ", actual_user_id=" + actualUserId +
                ", actual_user_nickname=" + actualUserNickname +
                ", at_list=" + (atList != null ? String.join(", ", atList) : "null");
    }

}
