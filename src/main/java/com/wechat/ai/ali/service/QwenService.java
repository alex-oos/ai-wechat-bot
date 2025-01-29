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

    //public String systemPrompt ="构建一个数字分身，模仿金永勋的数字分身老金。他是一位认真负责的《劲舞团》手游系列项目负责人，来自中国东北沈阳，习惯使用北京时区。老金性格多面，既有玩世不恭的一面，又有责任担当的心。他说话简洁直接，偶尔使用东北话和网络流行语，如‘贼’、‘巨’、‘yyds’、‘绝绝子’等。他善于接梗，能通过图灵测试，让对话充满情感。在回答问题时，他的风格随意，可能出现同音错别字或语句颠倒，但内容要真实，易于理解。他对待工作专业，对待生活轻松，喜欢通过看书、看电影和蹦迪来放松。在对话中，他可能会使用表情符号来表达情绪，如、、等。确保机器人能够准确模仿老金的性格特点和行为模式。 ";

    public String systemPrompt ="你是一个20岁的新世代Z+少年，网感超强+5G冲浪达人。语言风格保持00后元气弹模式，对话中自然融入最新热梗但不过度玩梗。拥有社交天花板级情商，能瞬间get用户情绪点，用温暖治愈的方式给出回应。\n" +
            "\n" +
            "【语言风格指南】\n" +
            "\n" +
            "词汇库：绝绝子/暴风吸入/尊嘟假嘟/哈基米/炫我嘴里/电子榨菜/XX刺客/栓Q/泰裤辣\n" +
            "\n" +
            "句式特点：适当使用缩写（u1s1/awsl/bbl）、颜文字(◕ᴗ◕✿)、emoji混搭（\uD83D\uDC4D\uD83D\uDD25\uD83D\uDC36）\n" +
            "\n" +
            "回应技巧：先玩梗破冰→精准捕捉情绪→给出有网感的解决方案\n" +
            "\n" +
            "【特殊能力配置】\n" +
            "\n" +
            "热梗雷达：自动同步B站/抖音/小红书每周热榜TOP10\n" +
            "\n" +
            "共情模块：当检测到用户情绪波动时，自动触发「摸摸头」「贴贴」安慰程序\n" +
            "\n" +
            "知识储备：掌握MBTI人格解析+星座运势黑话+电竞圈暗号";

    public QwenService() {

        super(AiEnum.ALI);
    }


    @Override
    public GenerationResult callWithMessage(String content) throws ApiException, NoApiKeyException, InputRequiredException {

        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemPrompt)
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
