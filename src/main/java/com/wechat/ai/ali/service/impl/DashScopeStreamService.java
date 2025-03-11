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
import com.wechat.bot.entity.BotConfig;
import com.wechat.util.FileUtil;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
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
public class DashScopeStreamService {

    public static StringBuilder fullContent = new StringBuilder();

    public static BotConfig botConfig = null;

    static {
        botConfig = FileUtil.readFile();
    }


    private static void handleGenerationResult(GenerationResult message) {

        String content = message.getOutput().getChoices().get(0).getMessage().getContent();
        fullContent.append(content);

    }

    public static void streamCallWithMessage(Generation gen, List<Message> messages) throws NoApiKeyException, ApiException, InputRequiredException {

        fullContent = new StringBuilder();
        GenerationParam param = buildGenerationParam(messages);
        Flowable<GenerationResult> result = gen.streamCall(param);
        result.blockingForEach(message -> handleGenerationResult(message));
        System.out.println("完整内容为: " + fullContent.toString());


    }

    private static GenerationParam buildGenerationParam(List<Message> messages) {

        return GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                //.apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .apiKey(botConfig.getDashscopeApiKey())
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model(botConfig.getModel())
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
    public String callWithMessage(List<Message> messages) throws ApiException, NoApiKeyException, InputRequiredException {

        Generation gen = new Generation();
        GenerationParam param = GenerationParam.builder()
                .apiKey(botConfig.getDashscopeApiKey())
                .model(botConfig.getModel())
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        GenerationResult result = gen.call(param);
        List<GenerationOutput.Choice> choices = result.getOutput().getChoices();
        ArrayList<String> messageList = new ArrayList<>();
        for (GenerationOutput.Choice choice : choices) {
            messageList.add(choice.getMessage().getContent());
        }
        String replayMsg = String.join("", messageList);
        messages.add(Message.builder().role(Role.ASSISTANT.getValue()).content(replayMsg).build());
        return replayMsg;
    }

    public static void main(String[] args) {

        try {

            Generation gen = new Generation();
            //String systemPrompt = "你是一个20岁的新世代将哥，网感超强+5G冲浪达人。语言风格保持00后元气弹模式，对话中自然融入最新热梗但不过度玩梗。拥有社交天花板级情商，能瞬间get用户情绪点，用温暖治愈的方式给出回应。【语言风格指南】词汇库：绝绝子/暴风吸入/尊嘟假嘟/哈基米/炫我嘴里/电子榨菜/XX刺客/栓Q/泰裤辣句式特点：适当使用缩写（u1s1/awsl/bbl）、颜文字(◕ᴗ◕✿)、emoji混搭（\\uD83D\\uDC4D\\uD83D\\uDD25\\uD83D\\uDC36）\\n\" +\n            \"\\n\" +\n            \"回应技巧：先玩梗破冰→精准捕捉情绪→给出有网感的解决方案\\n\" +\n            \"\\n\" +\n            \"【特殊能力配置】\\n\" +\n            \"\\n\" +\n            \"热梗雷达：自动同步B站/抖音/小红书每周热榜TOP10\\n\" +\n            \"\\n\" +\n            \"共情模块：当检测到用户情绪波动时，自动触发「摸摸头」「贴贴」安慰程序 知识储备：掌握MBTI人格解析+星座运势黑话+电竞圈暗号";

            Scanner scanner = new Scanner(System.in);
            Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("你是一个AI助理").build();
            List<Message> messages = new ArrayList<>();
            messages.add(systemMsg);
            System.out.println("请输入你想问的问题：");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                Message userMsg = Message.builder().role(Role.USER.getValue()).content(input).build();
                messages.add(userMsg);
                streamCallWithMessage(gen, messages);
                Message assistantMsg = Message.builder().role(Role.ASSISTANT.getValue()).content(fullContent.toString()).build();
                messages.add(assistantMsg);

            }


        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.error("An exception occurred: {}", e.getMessage());
        }
        System.exit(0);
    }


}
