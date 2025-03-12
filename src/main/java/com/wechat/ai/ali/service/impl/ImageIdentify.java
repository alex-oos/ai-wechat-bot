package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.Constants;
import com.wechat.ai.config.AiConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author Alex
 * @since 2025/3/12 17:45
 * <p>
 *     图片识别：
 * </p>
 */
@Slf4j
public class ImageIdentify {

    private static final String modelName = "qwen-vl-max-latest";


    static {
        Constants.baseHttpApiUrl = "https://dashscope-intl.aliyuncs.com/api/v1";

    }

    /**
     * 视觉理解：https://help.aliyun.com/zh/model-studio/user-guide/vision/
     *
     * @param content
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws UploadFileException
     */
    public static void simpleMultiModalConversationCall(List<Map<String, Object>> content)
            throws ApiException, NoApiKeyException, UploadFileException {

        MultiModalConversation conv = new MultiModalConversation();
        // 如果使用本地图像，请导入 import java.util.HashMap;，再为函数添加【String localPath】参数，并释放下面注释
        // String filePath = "file://"+localPath;
        MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("text", "You are a helpful assistant."))).build();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(content).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-
                //.apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                .model(MultiModalConversation.Models.QWEN_VL_PLUS)
                .messages(Arrays.asList(systemMessage, userMessage))
                .build();
        MultiModalConversationResult result = conv.call(param);
        System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
    }

    /**
     * 单轮对话
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws UploadFileException
     */
    public static void simpleMultiModalConversationCall()
            throws ApiException, NoApiKeyException, UploadFileException {

        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("text", "You are a helpful assistant."))).build();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg"),
                        Collections.singletonMap("text", "图中描绘的是什么景象?"))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                //.apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                .model("qwen-vl-max-latest")
                .messages(Arrays.asList(systemMessage, userMessage))
                .build();
        MultiModalConversationResult result = conv.call(param);
        System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
    }

    /**
     * 多轮对话
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws UploadFileException
     */
    public static void MultiRoundConversationCall() throws ApiException, NoApiKeyException, UploadFileException {

        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                .content(Arrays.asList(Collections.singletonMap("text", "You are a helpful assistant."))).build();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(Collections.singletonMap("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg"),
                        Collections.singletonMap("text", "图中描绘的是什么景象？"))).build();
        List<MultiModalMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model(modelName)
                .messages(messages)
                .build();
        MultiModalConversationResult result = conv.call(param);
        System.out.println("第一轮输出：" + result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));        // add the result to conversation
        messages.add(result.getOutput().getChoices().get(0).getMessage());
        MultiModalMessage msg = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(Collections.singletonMap("text", "做一首诗描述这个场景"))).build();
        messages.add(msg);
        param.setMessages((List) messages);
        result = conv.call(param);
        System.out.println("第二轮输出：" + result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
    }

    public static void main(String[] args) {

        try {
            //List<Map<String, Object>> list = Arrays.asList(
            //        // 第一张图像链接
            //        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"),
            //        // 如果使用本地图像，请并释放下面注释
            //        // new HashMap<String, Object>(){{put("image", filePath);}},
            //        // 第二张图像链接
            //        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/tiger.png"),
            //        // 第三张图像链接
            //        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/rabbit.png"),
            //        Collections.singletonMap("text", "这些图描绘了什么内容?"));
            //simpleMultiModalConversationCall(list);

            simpleMultiModalConversationCall();
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }

}
