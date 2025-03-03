package com.wechat.ai.deepseek.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import com.wechat.bot.entity.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alex
 * @since 2025/2/19 18:52
 * <p>
 * * 因 目前deepseek 官网不能使用，目前使用 阿里云的deepseek r1模型
 * * 文档如下：https://help.aliyun.com/zh/model-studio/developer-reference/deepseek?spm=a2c4g.11186623.help-menu-2400256.d_3_3_1_0.51834823WCKcfb#0c19e69319xc6
 * </p>
 */
@Slf4j
@Service
public class DeepSeekService extends AbstractAiService {


    @Resource
    private BotConfig botConfig;

    public DeepSeekService() {

        super(AiEnum.DEEPSEEK);
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

        List<String> messgaeList = new ArrayList<>();

        try {
            GenerationResult result = callWithMessage(content);
            System.out.println("思考过程：");
            System.out.println(result.getOutput().getChoices().get(0).getMessage().getReasoningContent());
            System.out.println("回复内容：");
            System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent());
            messgaeList.add(result.getOutput().getChoices().get(0).getMessage().getContent());
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            //System.err.println("An error occurred while calling the generation service: " + e.getMessage());
            log.error("An error occurred while calling the generation service: " + e.getMessage());
        }

        return messgaeList;
    }


}
