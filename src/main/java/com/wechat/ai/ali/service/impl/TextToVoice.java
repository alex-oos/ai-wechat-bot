package com.wechat.ai.ali.service.impl;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
import com.alibaba.dashscope.common.ResultCallback;
import com.wechat.ai.config.AiConfig;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * @author Alex
 * @since 2025/3/20 22:55
 * <p></p>
 */
public class TextToVoice {

    /**
     * 文本转语音
     * https://help.aliyun.com/zh/model-studio/developer-reference/sambert-java-api?spm=a2c4g.11186623.help-menu-2400256.d_3_3_7_1_0.17d27980BYpI39
     *
     * @param content
     * @return
     */
    public String textToVoice(String content) {

        CountDownLatch latch = new CountDownLatch(1);
        SpeechSynthesizer synthesizer = new SpeechSynthesizer();
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                // 若没有将API Key配置到环境变量中，需将下面这行代码注释放开，并将apiKey替换为自己的API Key
                .apiKey(AiConfig.botConfig.getDashscopeApiKey())
                .model("sambert-zhiqi-v1")
                .text(content)
                .sampleRate(48000)
                .enableWordTimestamp(true)
                .enablePhonemeTimestamp(true)
                .build();

        class ReactCallback extends ResultCallback<SpeechSynthesisResult> {

            @Override
            public void onEvent(SpeechSynthesisResult result) {

                if (result.getAudioFrame() != null) {
                    // do something with the audio frame
                    System.out.println("audio result length: " + result.getAudioFrame().array().length);
                }
                if (result.getTimestamp() != null) {
                    // do something with the timestamp
                    System.out.println("timestamp: " + result.getTimestamp());
                }
            }

            @Override
            public void onComplete() {
                // do something when the synthesis is done
                System.out.println("onComplete!");
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                // do something when an error occurs
                System.out.println("onError:" + e);
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
        try {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path voiceFilePath = Path.of("data", "voice", date, UUID.randomUUID().toString().concat(".wav"));
            voiceFilePath.toFile().getParentFile().mkdirs();
            Files.write(voiceFilePath, audioData.array(), StandardOpenOption.CREATE);
            return voiceFilePath.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        String s = new TextToVoice().textToVoice("今天天气怎么样？");
    }


}
