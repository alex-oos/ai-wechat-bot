package com.wechat.bot.contant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alex
 * @since 2025/1/27 00:14
 * <p></p>
 */
@Getter
@AllArgsConstructor
public enum MsgTypeEnum {

    TEXT(1, "text"),
    IMAGE(2, "image"),
    VOICE(3, "voice"),
    VIDEO(4, "video"),
    EMOJI(5, "emoji"),
    APPMSG(6, "appmsg"),
    MINIAPP(7, "miniapp"),
    FILE(8, "file"),
    FORWARD(9, "forward"),
    LINK(10, "link"),
    NAME_CARD(11, "namecard"),
    UNKNOWN(12, "unknown");

    private final Integer msgType;

    private final String type;


    public static MsgTypeEnum getMsgTypeEnum(Integer msgType) {

        for (MsgTypeEnum msgTypeEnum : MsgTypeEnum.values()) {
            if (msgTypeEnum.getMsgType().equals(msgType)) {
                return msgTypeEnum;
            }
        }
        return UNKNOWN;
    }


}
