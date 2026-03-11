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


/**
 * @author Alex
 * @since 2025/3/20 22:17
 * <p></p>
 */
@Slf4j
public class Image2Video {


    public static void main(String[] args) {

        try {
            image2video(null, null);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }

    /**
     * Create a video compositing task and wait for the task to complete.
     * https://help.aliyun.com/zh/model-studio/developer-reference/image-to-video-api-reference?spm=a2c4g.11186623.help-menu-2400256.d_3_3_5_0.242058ab3gKUDp#ecd1180f3c026
     */
    public static void image2video(String content, String imgUrl) throws ApiException, NoApiKeyException, InputRequiredException {


        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param =
                VideoSynthesisParam.builder()
                        .model("wanx2.1-i2v-turbo")
                        .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                        //.prompt("一只猫在草地上奔跑")
                        .prompt(content)
                        .imgUrl(imgUrl)
                        //.imgUrl("https://cdn.translate.alibaba.com/r/wanx-demo-1.png")
                        .build();
        // 异步调用
        VideoSynthesisResult task = vs.asyncCall(param);
        System.out.println(JsonUtils.toJson(task));
        System.out.println("please wait...");

        //获取结果
        // apiKey 已经配置在环境变量，因此这里可以设置为 null
        VideoSynthesisResult result = vs.wait(task, AiConfig.botConfig.getDashscopeApiKey());
        System.out.println(JsonUtils.toJson(result));
    }

}
