package com.wechat.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Alex
 * @since 2025/1/26 15:15
 * <p></p>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BotConfig {

    private String appId;

    private String token;

    private String channelType;

    private String dashscopeApiKey;

    private Boolean debug;

    private String baseUrl;

    private String callbackUrl;

    private String downloadUrl;

    private List<String> groupChatPrefix;

    private List<String> groupNameWhiteList;

    private boolean imageRecognition;

    private String model;

    private List<String> singleChatPrefix;

    private String singleChatReplyPrefix;

    private boolean speechRecognition;

    private String textToVoice;

    private boolean voiceReplyVoice;

    private String voiceToText;

    private String aiType;

    private String textToVoiceModel;

    private String ttsVoiceId;

    private String systemPrompt;

    /**
     * 图片生成前缀
     */
    private List<String> imageCreatePrefix;


    private String localhostIp;

}
