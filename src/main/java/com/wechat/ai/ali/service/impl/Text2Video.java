package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.wechat.ai.config.AiConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex
 * @since 2025/3/12 20:44
 * <p></p>
 */
@Slf4j
public class Text2Video {


    /**
     * Create a video compositing task and wait for the task to complete.
     * 参考地址：https://help.aliyun.com/zh/model-studio/developer-reference/text-to-video-api-reference?spm=a2c4g.11186623.help-menu-2400256.d_3_3_5_1.3e9e3e8ff8x0iM&scm=20140722.H_2865250._.OR_help-T_cn~zh-V_1#e9c6aaa30fykr
     */
    public static Map<String, Object> text2Video(String content) throws ApiException, NoApiKeyException, InputRequiredException {

        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param =
                VideoSynthesisParam.builder()
                        .model("wanx2.1-t2v-turbo")
                        .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                        .prompt(content)
                        .size("1280*720")
                        //.duration(10)
                        .build();

        // 异步调用
        VideoSynthesisResult task = vs.asyncCall(param);
        System.out.println(JsonUtils.toJson(task));
        log.info("please wait...");

        //获取结果
        VideoSynthesisResult result = vs.wait(task, AiConfig.botConfig.getDashscopeApiKey());
        System.out.println(JsonUtils.toJson(result));
        Map<String, Object> map = new HashMap<>();
        map.put("videoUrl", result.getOutput().getVideoUrl());
        map.put("videoDuration", result.getUsage().getVideoDuration());
        return map;
    }

    public static void main(String[] args) {

        try {
            text2Video("一只小猫在月光下奔跑");
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }

}
