package com.wechat.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

/**
 * @author Alex
 * @since 2025/3/21 14:58
 * <p></p>
 */
public class AudioDuration {
    public static double getDuration(File audioFile) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioInputStream.getFormat();

        // 获取音频帧数和帧率
        long frames = audioInputStream.getFrameLength();
        float frameRate = format.getFrameRate();

        // 计算时长（秒）
        double durationInSeconds = frames / frameRate;

        audioInputStream.close();
        return durationInSeconds;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("sample.wav");
        double duration = getDuration(file);
        System.out.printf("音频时长: %.2f 秒%n", duration);
    }
}
