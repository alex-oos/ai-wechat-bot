package com.wechat.ai.ali.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.wechat.ai.ali.service.impl.*;
import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Map;


/**
 * @author Alex
 * @since 2025/1/26 17:58
 * <p>
 * 通义千问  https://help.aliyun.com/zh/model-studio/developer-reference/use-qwen-by-calling-api?spm=a2c6h.13066369.question.6.66921a93YmGP7w
 * https://www.alibabacloud.com/help/zh/model-studio/developer-reference/use-qwen-by-calling-api
 * </p>
 */

@Slf4j
@Service
public class DashScopeService extends AbstractAiService {

    @Resource
    private BotConfig botConfig;

    public DashScopeService() {

        super(AiEnum.ALI);
    }


    /**
     * 流式请求
     *
     * @param content
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */
    public String streamMessage(Session session) {

        Instant now = Instant.now();
        Generation gen = new Generation();
        TextToText textToText = new TextToText();
        try {
            textToText.streamCallWithMessage(gen, session.getTextMessages());

            Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content(textToText.fullContent.toString()).build();
            session.addReply(assistantMsg.getContent());
        } catch (NoApiKeyException | InputRequiredException e) {
            log.error("An exception occurred: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        log.info("本次对话总耗时：{} ms", Instant.now().toEpochMilli() - now.toEpochMilli());
        return textToText.fullContent.toString();

    }

    @Override
    public String textToText(Session session) {
        // 流式消息
        return streamMessage(session);
        // 非流式消息
        //return TextToText.callWithMessage(session.getMessages());

    }

    @Override
    public Map<String, String> textToImage(String content) {

        return new TextToImage().asyncCall(content);

    }


    @Override
    public synchronized String imageToText(Session session) {

        try {
            return ImageIdentify.streamCall(session);
        } catch (NoApiKeyException | UploadFileException e) {
            log.error("AI 图片生成服务异常: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }


    }

    @Override
    public Map<String, Object> textToVideo(String content) {

        try {
            return Text2Video.text2Video(content);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Integer textToVoice(String content, String audioPath) {

        return new TextToVoice().textToVoice(content, audioPath);

    }


}
