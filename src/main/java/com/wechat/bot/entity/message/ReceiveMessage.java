package com.wechat.bot.entity.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 * @since 2025/1/26 17:41
 * <p>
 * 接收到的消息
 * </p>
 */
@Builder
@NoArgsConstructor()
@AllArgsConstructor
@Data
public class ReceiveMessage {

    private String appId;

    private String toWxid;

    private String fromWxid;

    private String content;

    private Integer status;


}
