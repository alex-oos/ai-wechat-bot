package com.wechat.ai.deepseek.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.wechat.ai.contant.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import com.wechat.bot.entity.BotConfig;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alex
 * @since 2025/2/19 18:52
 * <p></p>
 */
@Slf4j
@Service
public class DeepSeekService extends AbstractAiService {

    private static StringBuilder reasoningContent = new StringBuilder();

    private static StringBuilder finalContent = new StringBuilder();

    private static boolean isFirstPrint = true;

    @Resource
    private BotConfig botConfig;

    public DeepSeekService() {

        super(AiEnum.DEEPSEEK);
    }

    /**
     * 因 目前deepseek 官网不能使用，目前使用 阿里云的deepseek r1模型
     * 文档如下：https://help.aliyun.com/zh/model-studio/developer-reference/deepseek?spm=a2c4g.11186623.help-menu-2400256.d_3_3_1_0.51834823WCKcfb#0c19e69319xc6
     * 使用流式输出
     *
     * @param content
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */

    private static void handleGenerationResult(GenerationResult message) {

        String reasoning = message.getOutput().getChoices().get(0).getMessage().getReasoningContent();
        String content = message.getOutput().getChoices().get(0).getMessage().getContent();

        if (!reasoning.isEmpty()) {
            reasoningContent.append(reasoning);
            if (isFirstPrint) {
                System.out.println("====================思考过程====================");
                isFirstPrint = false;
            }
            System.out.print(reasoning);
        }

        if (!content.isEmpty()) {
            finalContent.append(content);
            if (!isFirstPrint) {
                System.out.println("\n====================完整回复====================");
                isFirstPrint = true;
            }
            System.out.print(content);
        }
    }

    private static GenerationParam buildGenerationParam(Message userMsg) {

        return GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("deepseek-r1")
                .messages(Arrays.asList(userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(true)
                .build();
    }

    public static void streamCallWithMessage(Generation gen, Message userMsg)
            throws NoApiKeyException, ApiException, InputRequiredException {

        GenerationParam param = buildGenerationParam(userMsg);
        Flowable<GenerationResult> result = gen.streamCall(param);
        result.blockingForEach(message -> handleGenerationResult(message));
    }

    public static void main(String[] args) {

        try {
            Generation gen = new Generation();
            Message userMsg = Message.builder().role(Role.USER.getValue()).content("你是谁？").build();
            streamCallWithMessage(gen, userMsg);
            //打印最终结果
            if (reasoningContent.length() > 0) {
                System.out.println("\n====================完整回复====================");
                System.out.println(finalContent.toString());
            }
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.error("An exception occurred: {}", e.getMessage());
        }
        System.exit(0);
    }

    @Override
    public List<String> textToText(String content) {

        Generation gen = new Generation();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(content).build();
        try {
            streamCallWithMessage(gen, userMsg);
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (InputRequiredException e) {
            throw new RuntimeException(e);
        }


        return List.of();
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


}
