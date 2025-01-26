package com.wechat.bot.entity.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 * @since 2025/1/26 17:43
 * <p></p>
 */
@Builder
@NoArgsConstructor()
@AllArgsConstructor
@Data
public class SendTextMessage {

    private String appId;

    private String toWxid;

    private String ats;

    private String content;


    //String appId, String toWxid, String content, String ats

}
