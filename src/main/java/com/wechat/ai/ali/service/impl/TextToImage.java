package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.common.TaskStatus;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.wechat.ai.config.AiConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author Alex
 * @since 2025/3/20 22:36
 * <p></p>
 */
@Slf4j
public class TextToImage {

    public Map<String, String> asyncCall(String content) {

        System.out.println("---create task----");
        String taskId = this.createAsyncTask(content);
        System.out.println("---wait task done then return image url----");
        return this.waitAsyncTask(taskId);
    }


    /**
     * 创建异步任务
     *
     * @return taskId
     */
    public String createAsyncTask(String content) {

        //String prompt = "一间有着精致窗户的花店，漂亮的木质门，摆放着花朵";
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        //.apiKey(System.getenv("DASHSCOPE_API_KEY"))
                        .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                        .model("wanx2.1-t2i-turbo")
                        .prompt(content)
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            result = imageSynthesis.asyncCall(param);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
        String taskId = result.getOutput().getTaskId();
        System.out.println("taskId=" + taskId);
        return taskId;
    }


    /**
     * 等待异步任务结束
     *
     * @param taskId 任务id
     */
    public Map<String, String> waitAsyncTask(String taskId) {

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            result = imageSynthesis.wait(taskId, (AiConfig.botConfig.getDashscopeApiKey()));
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
        System.out.println(JsonUtils.toJson(result.getOutput()));
        List<Map<String, String>> results = result.getOutput().getResults();
        return results.get(0);
    }


    public Map<String, String> sync(String content) {

        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                        .model("wanx2.1-t2i-turbo")
                        .prompt(content)
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            log.info("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        //System.out.println(JsonUtils.toJson(result));
        log.info(JsonUtils.toJson(result));
        String taskStatus = result.getOutput().getTaskStatus();
        if (!TaskStatus.SUCCEEDED.getValue().equals(taskStatus)) {
            log.error("taskStatus is not SUCCESS, taskStatus: {}", taskStatus);
            return null;
        }
        List<Map<String, String>> results = result.getOutput().getResults();
        return results.get(0);
    }

    public static void main(String[] args) {

        TextToImage main = new TextToImage();
        main.asyncCall("一间有着精致窗户的花店，漂亮的木质门，摆放着花朵");
    }

}
