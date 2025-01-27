package com.wechat.ai.ali.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.wechat.ai.ali.config.ALiConfig;
import com.wechat.ai.ali.service.impl.AliService;
import com.wechat.ai.contant.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Alex
 * @since 2025/1/26 17:58
 * <p></p>
 */

@Slf4j
@Service
public class QwenService extends AbstractAiService implements AliService {

    @Autowired
    private ALiConfig qwenConfig;

    public QwenService() {

        super(AiEnum.ALI);
    }


    @Override
    public GenerationResult callWithMessage(String content) throws ApiException, NoApiKeyException, InputRequiredException {

        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一个AI助理")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(content)
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(qwenConfig.getApiKey())
                .model(qwenConfig.getModel())
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
            //log.error(e.getMessage());
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String textToImage(String content) {

        return "";
    }

    @Override
    public String imageToText(String content) {

        return "";
    }

    @Override
    public String imageToImage(String content) {

        return "";
    }

    @Override
    public String imageToImage(String content, String style) {

        return "";
    }

    @Override
    public String imageToImage(String content, String style, String prompt) {

        return "";
    }

    @Override
    public String imageToImage(String content, String style, String prompt, String negativePrompt) {

        return "";
    }

    @Override
    public Boolean checkIsEnabled() {

        return false;
    }


}
