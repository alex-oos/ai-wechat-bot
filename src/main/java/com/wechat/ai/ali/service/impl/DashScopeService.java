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
 *    通义千问  https://help.aliyun.com/zh/model-studio/developer-reference/use-qwen-by-calling-api?spm=a2c6h.13066369.question.6.66921a93YmGP7w
 *    https://www.alibabacloud.com/help/zh/model-studio/developer-reference/use-qwen-by-calling-api
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


    public GenerationResult callWithMessage(String content) throws ApiException, NoApiKeyException, InputRequiredException {

        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(botConfig.getSystemPrompt())
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(content)
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(botConfig.getDashscopeApiKey())
                .model(botConfig.getModel())
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }

    @Override
    public List<String> textToText(String content) {

        try {
            GenerationResult result = callWithMessage(content);
            List<GenerationOutput.Choice> choices = result.getOutput().getChoices();
            ArrayList<String> messageList = new ArrayList<>();
            for (GenerationOutput.Choice choice : choices) {
                //System.out.println(choice.getMessage().getContent());
                messageList.add(choice.getMessage().getContent());
            }
            //System.out.println(JsonUtils.toJson(result));
            return messageList;
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            log.error("An error occurred while calling the generation service:{}",e.getMessage());
            //System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<String> textToImage(String content) {

        //String prompt = "一间有着精致窗户的花店，漂亮的木质门，摆放着花朵";
        String prompt = content;
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(botConfig.getDashscopeApiKey())
                        .model("wanx2.1-t2i-turbo")
                        .prompt(prompt)
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
        List<Map<String, String>> results = result.getOutput().getResults();
        List<String> imageUrlList = new ArrayList<>();
        results.forEach(e -> {
            imageUrlList.add(e.get("url"));
        });

        return imageUrlList;
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
