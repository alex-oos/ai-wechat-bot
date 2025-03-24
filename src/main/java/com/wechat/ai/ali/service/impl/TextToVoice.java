package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
import com.alibaba.dashscope.common.ResultCallback;
import com.wechat.ai.config.AiConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;

/**
 * @author Alex
 * @since 2025/3/20 22:55
 * <p></p>
 */
@Slf4j
public class TextToVoice {

    public static void main(String[] args) {

        new TextToVoice().textToVoice("今天天气怎么样？", "test.pcm");
    }

    /**
     * 文本转语音
     * https://help.aliyun.com/zh/model-studio/developer-reference/sambert-java-api?spm=a2c4g.11186623.help-menu-2400256.d_3_3_7_1_0.17d27980BYpI39
     *
     * @param content
     * @return
     */
    public Integer textToVoice(String content, String audioPath) {

        CountDownLatch latch = new CountDownLatch(1);
        SpeechSynthesizer synthesizer = new SpeechSynthesizer();
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                // 若没有将API Key配置到环境变量中，需将下面这行代码注释放开，并将apiKey替换为自己的API Key
                .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                .model("sambert-zhiqi-v1")
                .text(content)
                .sampleRate(24000)
                .enableWordTimestamp(true)
                .enablePhonemeTimestamp(true)
                .format(SpeechSynthesisAudioFormat.PCM)
                .build();

        class ReactCallback extends ResultCallback<SpeechSynthesisResult> {

            @Override
            public void onEvent(SpeechSynthesisResult result) {

                if (result.getAudioFrame() != null) {
                    // do something with the audio frame
                    //System.out.println("audio result length: " + result.getAudioFrame().array().length);
                }
                if (result.getTimestamp() != null) {
                    // do something with the timestamp
                    //System.out.println("timestamp: " + result.getTimestamp());
                }
            }

            @Override
            public void onComplete() {
                // do something when the synthesis is done
                //System.out.println("onComplete!");
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                // do something when an error occurs
                //System.out.println("onError:" + e);
                log.error("e{}", e.getMessage());
                latch.countDown();
            }

        }

        synthesizer.call(param, new ReactCallback());
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //拿到所有的结果 返回一个流式合成片段的增量二进制音频数据，可能为空。
        ByteBuffer audioData = synthesizer.getAudioData();
        // 将结果写入
        if (audioData == null) {
            log.warn("audioData为空");
            return null;
        }
        try {
            Files.write(Paths.get(audioPath), audioData.array(), StandardOpenOption.CREATE);
            log.info("音频文件生成{}", audioPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int videoDuration = synthesizer.getTimestamps().stream().mapToInt(e -> e.getEndTime() - e.getBeginTime()).sum();
        log.info("音频时长{}", videoDuration);
        return videoDuration;
    }

}
