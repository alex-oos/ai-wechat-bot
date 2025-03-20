package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.wechat.ai.config.AiConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex
 * @since 2025/3/12 20:44
 * <p></p>
 */
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
                        .prompt(content)
                        .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                        .size("1280*720")
                        .build();
        System.out.println("please wait...");
        VideoSynthesisResult result = vs.call(param);
        //System.out.println(result.getOutput().getVideoUrl());
        System.out.println(JsonUtils.toJson(result));
        Map<String, Object> map = new HashMap<>();
        map.put("videoUrl", result.getOutput().getVideoUrl());
        map.put("videoDuration", result.getUsage().getVideoDuration());
        return map;
        //return JsonUtils.toJson(result);
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
