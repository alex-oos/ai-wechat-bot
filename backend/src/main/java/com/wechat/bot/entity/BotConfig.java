package com.wechat.bot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
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
@TableName(value = "bot_config")
public class BotConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appId;

    private String token;

    private String channelType;

    private String dashscopeApiKey;

    private Boolean debug;

    private String baseUrl;

    private String callbackUrl;

    private String downloadUrl;

    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> groupChatPrefix;

    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> groupNameWhiteList;

    private Boolean imageRecognition;

    private String model;

    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> singleChatPrefix;

    private String singleChatReplyPrefix;

    private Boolean speechRecognition;

    private String textToVoice;

    private Boolean voiceReplyVoice;

    private String voiceToText;

    private String aiType;

    private String textToVoiceModel;

    private String ttsVoiceId;

    private String systemPrompt;

    /**
     * 图片生成前缀
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> imageCreatePrefix;


    private String localhostIp;

}
