package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.common.TaskStatus;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
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
        TextToText dashScopeStreamService = new TextToText();
        try {
            dashScopeStreamService.streamCallWithMessage(gen, session.getTextMessages());

            Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content(dashScopeStreamService.fullContent.toString()).build();
            session.addReply(assistantMsg.getContent());
        } catch (NoApiKeyException | InputRequiredException e) {
            log.error("An exception occurred: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        log.info("本次对话总耗时：{} ms", Instant.now().toEpochMilli() - now.toEpochMilli());
        return dashScopeStreamService.fullContent.toString();

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

        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(botConfig.getDashscopeApiKey())
                        .model("wanx2.1-t2i-turbo")
                        .prompt(content)
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            log.info("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        //System.out.println(JsonUtils.toJson(result));
        log.info(JsonUtils.toJson(result));
        String taskStatus = result.getOutput().getTaskStatus();
        if (!TaskStatus.SUCCEEDED.getValue().equals(taskStatus)) {
            log.error("taskStatus is not SUCCESS, taskStatus: {}", taskStatus);
            return null;
        }
        List<Map<String, String>> results = result.getOutput().getResults();

        return results.get(0);
    }


    @Override
    public String imageToText(Session session) {

        try {
            return ImageIdentify.streamCall(session);
        } catch (NoApiKeyException | UploadFileException e) {
            log.error("AI 图片生成服务异常: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }


    }

    @Override
    public String textToVideo(String content) {

        try {
            Text2Video.text2Video(content);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }

        return "";
    }


}
