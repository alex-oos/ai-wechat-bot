package com.wechat.bot.service.impl;

import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.factory.AiServiceFactory;
import com.wechat.ai.service.AIService;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.service.ReplyMsgService;
import com.wechat.gewechat.service.MessageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Alex
 * @since 2025/2/18 21:37
 * <p></p>
 */
@Slf4j
@Service
public class ReplyMsgServiceImpl implements ReplyMsgService {


    @Resource(name = "commonThreadPool")
    private TaskExecutor executor;

    @Resource
    private BotConfig botconfig;

    private AIService aiService;

    private Session session;

    @Override
    public void replyType(ChatMessage chatMessage, Session session1) {

        session = session1;
        aiService = chooseAiService();
        // 判断类型
        switch (chatMessage.getCtype()) {
            case TEXT:
                this.replyTextMsg(chatMessage);
                break;
            case IMAGE:
                // TODO 先过滤掉，图片识别，后期再来做
                if (chatMessage.getContent().contains("xml")){
                    return;
                }
                this.replyImageMsg(chatMessage);
                break;
            case VOICE:
                this.replyAudioMsg(chatMessage);
                break;
            case VIDEO:
                this.replyVideoMsg(chatMessage);
                break;
            case IMAGERECOGNITION:
                this.imageRecognition(chatMessage);
                break;
            default:
                break;
        }
    }

    @Override
    public void replyTextMsg(ChatMessage chatMessage) {

        String replayMsg = aiService.textToText(session);
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), replayMsg, chatMessage.getToUserId());
        log.info("消息回复成功，回复人：{}，回复内容为：{}", chatMessage.getFromUserNickname(), replayMsg);
    }


    @Override
    public void replyImageMsg(ChatMessage chatMessage) {

        Map<String, String> map = aiService.textToImage(chatMessage.getContent());
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), map.get("actual_prompt"), chatMessage.getToUserId());
        MessageApi.postImage(chatMessage.getAppId(), chatMessage.getFromUserId(), map.get("url"));


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
    public void imageRecognition(ChatMessage chatMessage) {

        String s = aiService.imageToText(session);
        log.info("图片识别成功，图片内容：{}", s);
        MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), s, chatMessage.getToUserId());
    }


    @Override
    public AIService chooseAiService() {

        // 找到正常的服务，然后取出枚举值
        //AiEnum aiEnum = AiEnum.getByBotType(Objects.requireNonNull(FileUtil.readFile()).getAiType());
        AiEnum aiEnum = null;
        if (botconfig != null) {
            aiEnum = AiEnum.getByBotType(botconfig.getAiType());
        }
        return AiServiceFactory.getAiService(aiEnum);
    }

}
