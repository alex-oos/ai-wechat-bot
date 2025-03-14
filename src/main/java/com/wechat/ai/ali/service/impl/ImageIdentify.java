package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.wechat.ai.config.AiConfig;
import com.wechat.ai.session.Session;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author Alex
 * @since 2025/3/12 17:45
 * <p>
 * 视觉理解：
 * 官方文档地址：https://help.aliyun.com/zh/model-studio/user-guide/vision/?spm=a2c4g.11186623.help-menu-2400256.d_1_0_1.17aa19f5Ruc7yg#7b99d17fcfpz3
 * </p>
 */
@Slf4j
public class ImageIdentify {


    public static StringBuilder replayContent = new StringBuilder();

    public static String streamCall(Session session) throws ApiException, NoApiKeyException, UploadFileException {

        replayContent.setLength(0);
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalConversationParam param = session.getMultiModalConversationParam();
        List<MultiModalMessage> messages = session.getImageMessages();
        if (param == null) {
            param = MultiModalConversationParam.builder()
                    .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                    .model("qwen-vl-max-latest")
                    .messages(messages)
                    .incrementalOutput(true)
                    .build();
            session.setMultiModalConversationParam(param);
        } else {
            param.setMessages((List) messages);
        }
        Flowable<MultiModalConversationResult> result = conv.streamCall(param);
        result.blockingForEach(item -> {
            if (!item.getOutput().getChoices().get(0).getMessage().getContent().isEmpty()) {
                replayContent.append(item.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
            }
        });
        MultiModalMessage assistantMsg = MultiModalMessage.builder().role(Role.ASSISTANT.getValue())
                .content(List.of(Collections.singletonMap("text", replayContent.toString()))).build();
        messages.add(assistantMsg);
        System.out.println("结果为：" + replayContent.toString());
        return replayContent.toString();
    }

    public static MultiModalConversationParam streamCall(List<MultiModalMessage> messages, MultiModalConversationParam param) throws ApiException, NoApiKeyException, UploadFileException {

        replayContent.setLength(0);
        MultiModalConversation conv = new MultiModalConversation();

        if (param == null) {
            param = MultiModalConversationParam.builder()
                    .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                    .model("qwen-vl-max-latest")
                    .messages(messages)
                    .incrementalOutput(true)
                    .build();
        } else {
            param.setMessages((List) messages);
        }
        Flowable<MultiModalConversationResult> result = conv.streamCall(param);
        result.blockingForEach(item -> {
            if (!item.getOutput().getChoices().get(0).getMessage().getContent().isEmpty()) {
                replayContent.append(item.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
            }
        });
        System.out.println(replayContent.toString());
        MultiModalMessage assistantMsg = MultiModalMessage.builder().role(Role.ASSISTANT.getValue())
                .content(List.of(Collections.singletonMap("text", replayContent.toString()))).build();
        messages.add(assistantMsg);
        return param;
    }

    public static MultiModalConversationParam multiModalConversation(List<MultiModalMessage> messages, MultiModalConversationParam param) throws ApiException, NoApiKeyException, UploadFileException {

        MultiModalConversation conv = new MultiModalConversation();
        if (param == null) {
            param = MultiModalConversationParam.builder()
                    // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                    .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                    .model("qwen-vl-max-latest")
                    .messages(messages)
                    .build();
        } else {
            param.setMessages((List) messages);
        }
        MultiModalConversationResult result = conv.call(param);
        Object content = result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
        System.out.println("输出：" + result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
        MultiModalMessage assistantMsg = MultiModalMessage.builder().role(Role.ASSISTANT.getValue())
                .content(List.of(Collections.singletonMap("text", content))).build();// add the result to conversation
        messages.add(assistantMsg);
        return param;
    }


    public static void main(String[] args) {

        try {


            MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                    .content(Arrays.asList(Collections.singletonMap("text", "你是一个图片识别助手，请根据图片描述，输出图片的描述信息"))).build();
            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(Arrays.asList(Collections.singletonMap("image", "file:///home/alex/github/wechat-bot/data/images/20250313/wx_00zu2UQIaF1sJayYl2LHg/99954413-9d4a-4ca3-b8ac-ff2e9bc0c9b2.png"),
                            Collections.singletonMap("text", "图中描绘的是什么景象？"))).build();
            List<MultiModalMessage> messages = new ArrayList<>();
            Collections.addAll(messages, systemMessage, userMessage);
            //MultiModalConversationParam multiModalConversationParam = streamCall(messages, null);
            //MultiModalConversationParam multiModalConversationParam = multiModalConversation(messages, null);
            Session session = new Session(UUID.randomUUID().toString(), "你是一个图片识别助手，请根据图片描述，输出图片的描述信息");
            session.setImageMessages(messages);
            streamCall(session);
            MultiModalMessage userMsg1 = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(List.of(Collections.singletonMap("text", "做一首诗描述这个场景"))).build();
            messages.add(userMsg1);
            //multiModalConversation(messages, multiModalConversationParam);
            session.setImageMessages(messages);
            streamCall(session);
            MultiModalMessage userMsg2 = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(List.of(Collections.singletonMap("text", "写一篇文章来描述！"))).build();
            messages.add(userMsg2);
            //multiModalConversation(messages, multiModalConversationParam);
            session.setImageMessages(messages);

            streamCall(session);


        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }

    }


}
