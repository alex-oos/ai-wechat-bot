package com.wechat.bot.entity.message.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 * @since 2025/1/27 00:08
 * <p></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyTextMessage {

    private String appId;

    private String toWxid;

    private String ats;

    private String content;

}
