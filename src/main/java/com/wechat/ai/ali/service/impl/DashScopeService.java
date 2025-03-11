package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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
    public Session streamMessage(Session session) {

        Generation gen = new Generation();
        try {
            DashScopeStreamService.streamCallWithMessage(gen, session.getMessages());
            Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content(DashScopeStreamService.fullContent.toString()).build();
            session.addReply(assistantMsg.getContent());
        } catch (NoApiKeyException | InputRequiredException e) {
            log.error("An exception occurred: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return session;

    }

    @Override
    public String textToText(Session session) {

        session = streamMessage(session);
        return session.getMessages().get(session.getMessages().size() - 1).getContent();

    }

    @Override
    public List<String> textToImage(String content) {

        //String prompt = "一间有着精致窗户的花店，漂亮的木质门，摆放着花朵";
        //String prompt = content;
        //ImageSynthesisParam param =
        //        ImageSynthesisParam.builder()
        //                .apiKey(botConfig.getDashscopeApiKey())
        //                .model("wanx2.1-t2i-turbo")
        //                .prompt(prompt)
        //                .n(1)
        //                .size("1024*1024")
        //                .build();
        //
        //ImageSynthesis imageSynthesis = new ImageSynthesis();
        //ImageSynthesisResult result = null;
        //try {
        //    System.out.println("---sync call, please wait a moment----");
        //    result = imageSynthesis.call(param);
        //} catch (ApiException | NoApiKeyException e) {
        //    throw new RuntimeException(e.getMessage());
        //}
        //System.out.println(JsonUtils.toJson(result));
        //List<Map<String, String>> results = result.getOutput().getResults();
        //List<String> imageUrlList = new ArrayList<>();
        //results.forEach(e -> {
        //    imageUrlList.add(e.get("url"));
        //});

        return null;
    }


    @Override
    public String imageToText(String content) {

        return super.imageToText(content);
    }

    @Override
    public String imageToImage(String content) {

        return super.imageToImage(content);
    }

    @Override
    public String imageToImage(String content, String style) {

        return super.imageToImage(content, style);
    }

    @Override
    public String imageToImage(String content, String style, String prompt) {

        return super.imageToImage(content, style, prompt);
    }

    @Override
    public String imageToImage(String content, String style, String prompt, String negativePrompt) {

        return super.imageToImage(content, style, prompt, negativePrompt);
    }


}
