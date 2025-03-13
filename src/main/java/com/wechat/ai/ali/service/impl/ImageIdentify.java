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
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public static String streamCall(List<MultiModalMessage> messages) throws ApiException, NoApiKeyException, UploadFileException {

        replayContent.setLength(0);
        MultiModalConversation conv = new MultiModalConversation();

        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                .model("qwen-vl-max-latest")
                .messages(messages)
                .incrementalOutput(true)
                .build();
        Flowable<MultiModalConversationResult> result = conv.streamCall(param);
        result.blockingForEach(item -> {
            try {
                replayContent.append(item.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
            } catch (Exception e) {
                System.exit(0);
            }
        });
        System.out.println(replayContent);
        return replayContent.toString();
    }

    public static void main(String[] args) {

        try {

            // must create mutable map.
            MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                    .content(Arrays.asList(Collections.singletonMap("text", "You are a helpful assistant."))).build();
            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(Arrays.asList(Collections.singletonMap("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg"),
                            Collections.singletonMap("text", "图中描绘的是什么景象？"))).build();
            List<MultiModalMessage> messages = Arrays.asList(systemMessage, userMessage);
            streamCall(messages);
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }

    }


}
