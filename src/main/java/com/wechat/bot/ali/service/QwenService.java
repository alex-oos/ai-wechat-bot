package com.wechat.bot.ali.service;

import com.wechat.bot.ali.config.QwenConfig;
import com.wechat.bot.ali.service.impl.AliService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Arrays;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;

/**
 * @author Alex
 * @since 2025/1/26 17:58
 * <p></p>
 */

@Service
public class QwenService implements AliService {

    @Resource
    private QwenConfig qwenConfig;

    @Override
    public GenerationResult callWithMessage(String content) throws ApiException, NoApiKeyException, InputRequiredException {

        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
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
    public String textToText(String content) {

        try {
            GenerationResult result = callWithMessage(content);
            System.out.println(JsonUtils.toJson(result));
            return result.getRequestId();
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
        return null;
    }


}
