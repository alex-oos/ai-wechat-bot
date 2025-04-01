package com.wechat.bot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alex
 * @since 2025/1/27 00:14
 * <p></p>
 */
@Getter
@AllArgsConstructor
public enum MsgTypeEnum {

    TEXT(1, "text", "文本消息"),
    IMAGE(3, "image", "图片消息"),
    VOICE(34, "voice", "语音消息"),
    VIDEO(43, "video", "视频消息"),
    EMOJI(47, "emoji", null),
    APPMSG(49, "appmsg", "引用消息"),
    MINIAPP(7, "miniapp", null),
    FILE(8, "file", null),
    FORWARD(9, "forward", null),
    LINK(10, "link", null),
    NAME_CARD(11, "namecard", null),
    UNKNOWN(-1, "unknown", null),
    IMAGERECOGNITION(12, "imagerecognition", "图片识别消息"),
    TAKESHOT(10002, "takeshot", "拍一拍下消息");



    private final Integer msgType;

    private final String type;

    private final String desc;


    public static MsgTypeEnum getMsgTypeEnum(Integer msgType) {

        for (MsgTypeEnum msgTypeEnum : MsgTypeEnum.values()) {
            if (msgTypeEnum.getMsgType().equals(msgType)) {
                return msgTypeEnum;
            }
        }
        return UNKNOWN;
    }


}
