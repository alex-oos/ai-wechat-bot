package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.wechat.ai.config.AiConfig;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Alex
 * @since 2025/3/11 22:46
 * <p>
 * 流式请求
 * </p>
 */
@Slf4j
public class TextToText {


    public  StringBuilder fullContent = new StringBuilder();


    private  GenerationParam buildGenerationParam(List<Message> messages) {

        return GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                //.apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model(AiConfig.botConfig.getModel())
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(true)
                .build();
    }

    /**
     * 正常请求
     * https://help.aliyun.com/zh/model-studio/user-guide/multi-round-conversation?scm=20140722.S_help%40%40%E6%96%87%E6%A1%A3%40%402866125.S_BB1%40bl%2BRQW%40ag0%2BBB2%40ag0%2Bhot%2Bos0.ID_2866125-RL_%E5%A4%9A%E8%BD%AE%E5%AF%B9%E8%AF%9D-LOC_doc%7EUND%7Eab-OR_ser-PAR1_212a5d3e17417111139226722d75c4-V_4-P0_0-P1_0&spm=a2c4g.11186623.help-search.i44#c0391875f17vq
     *
     * @param content
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */
    public  String callWithMessage(List<Message> messages) {

        Instant now = Instant.now();
        Generation gen = new Generation();
        GenerationParam param = GenerationParam.builder()
                .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                .model(AiConfig.botConfig.getModel())
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        GenerationResult result = null;
        try {
            result = gen.call(param);
        } catch (NoApiKeyException | InputRequiredException e) {
            log.error("An exception occurred: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        List<GenerationOutput.Choice> choices = result.getOutput().getChoices();
        ArrayList<String> messageList = new ArrayList<>();
        for (GenerationOutput.Choice choice : choices) {
            messageList.add(choice.getMessage().getContent());
        }
        String replayMsg = String.join("", messageList);
        messages.add(Message.builder().role(Role.ASSISTANT.getValue()).content(replayMsg).build());
        log.info("本次请求耗时：{}ms", Duration.between(now, Instant.now()).toMillis());
        return replayMsg;
    }

    private  void handleGenerationResult(GenerationResult message) {

        String content = message.getOutput().getChoices().get(0).getMessage().getContent();
        fullContent.append(content);

    }

    /**
     * 流式请求，响应更快一些
     *
     * @param gen
     * @param messages
     * @throws NoApiKeyException
     * @throws ApiException
     * @throws InputRequiredException
     */
    public  void streamCallWithMessage(Generation gen, List<Message> messages) throws NoApiKeyException, ApiException, InputRequiredException {

        fullContent.setLength(0);
        GenerationParam param = buildGenerationParam(messages);
        Flowable<GenerationResult> result = gen.streamCall(param);
        result.blockingForEach(this::handleGenerationResult);


    }

    public static void main(String[] args) {

        try {

            Generation gen = new Generation();
            Scanner scanner = new Scanner(System.in);
            Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("你是一个AI助理").build();
            List<Message> messages = new ArrayList<>();
            messages.add(systemMsg);
            System.out.println("请输入你想问的问题：");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                Message userMsg = Message.builder().role(Role.USER.getValue()).content(input).build();
                messages.add(userMsg);
                TextToText textToText = new TextToText();
                textToText.streamCallWithMessage(gen, messages);
                Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content(textToText.fullContent.toString()).build();
                System.out.println(textToText.fullContent.toString());
                messages.add(assistantMsg);

            }


        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.error("An exception occurred: {}", e.getMessage());
        }
        System.exit(0);
    }


}
