package com.wechat.util;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

/**
 * @author Alex
 * @since 2025/3/21 15:11
 * <p></p>
 */
public class VideoDuration {

    public static int getAudioDurationMs(String filePath) {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET); // 关闭FFmpeg日志
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
            grabber.start();
            long durationMicroseconds = grabber.getLengthInTime();
            return (int) (durationMicroseconds / 1000); // 微秒转毫秒
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {

        String silkFilePath = "demo.wav";
        System.out.println("音频时长: " + getAudioDurationMs(silkFilePath) + " ms");
    }

}
