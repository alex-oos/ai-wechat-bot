package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.contant.AiEnum;
import com.wechat.ai.factory.AiServiceFactory;
import com.wechat.ai.service.AIService;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.service.ReplyMsgService;
import com.wechat.gewechat.service.MessageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alex
 * @since 2025/2/18 21:37
 * <p></p>
 */
@Slf4j
@Service
public class ReplyMsgServiceImpl implements ReplyMsgService {

    @Resource
    private ThreadPoolTaskExecutor executor;

    @Resource
    private BotConfig botconfig;

    private AIService aiService;

    @Override
    public void replyType(ChatMessage chatMessage) {

        aiService = chooseAiService();
        // 判断类型
        switch (chatMessage.getCtype()) {
            case TEXT:
                this.replyTextMsg(chatMessage);
                break;
            case IMAGE:
                this.replyImageMsg(chatMessage);
                break;
            case VOICE:
                this.replyAudioMsg(chatMessage);
                break;
            case VIDEO:
                this.replyVideoMsg(chatMessage);
                break;
            default:
                break;
        }
    }

    @Override
    public void replyTextMsg(ChatMessage chatMessage) {


        CompletableFuture.supplyAsync(() -> {
            log.info("请求AI服务");
            return aiService.textToText(chatMessage.getContent());
        }, executor).thenApplyAsync((res) -> {
            res.forEach(msg -> {
                log.info("请求gewechat服务：{}", msg);
                JSONObject jsonObject = MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), msg, chatMessage.getToUserId());
                if (jsonObject.getInteger("ret") == 200) {
                    log.info("gewechat服务回复成功");
                }
            });
            return null;
        }, executor);
    }


    @Override
    public void replyImageMsg(ChatMessage chatMessage) {


        CompletableFuture.supplyAsync(() -> {
            log.info("请求AI服务");
            return aiService.textToImage(chatMessage.getContent());
        }, executor).thenApplyAsync((res) -> {
            res.forEach(msg -> {
                log.info("请求gewechat服务：{}", msg);
                JSONObject jsonObject = MessageApi.postImage(chatMessage.getAppId(), chatMessage.getFromUserId(), msg);
                if (jsonObject.getInteger("ret") == 200) {
                    log.info("gewechat服务回复成功");
                }
            });
            return null;
        }, executor);

    }

    @Override
    public void replyVideoMsg(ChatMessage chatMessage) {

    }

    @Override
    public void replyFileMsg(ChatMessage chatMessage) {

    }

    @Override
    public void replyAudioMsg(ChatMessage chatMessage) {

    }

    @Override
    public void replyLocationMsg(ChatMessage chatMessage) {

    }

    @Override
    public void replyLinkMsg(ChatMessage chatMessage) {

    }


    @Override
    public AIService chooseAiService() {

        // 找到正常的服务，然后取出枚举值
        AiEnum aiEnum = AiEnum.getByBotType(botconfig.getBotType());
        AIService aiService = AiServiceFactory.getAiService(aiEnum);
        return aiService;
    }

}
