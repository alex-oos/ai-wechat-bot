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

    //public static long getAudioDurationMs(String filePath) {
    //    // 强制注册所有FFmpeg编解码器（确保silk格式支持）
    //    avutil.av_log_set_level(avutil.AV_LOG_QUIET); // 关闭FFmpeg日志
    //    try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
    //        grabber.start();
    //        long durationMicroseconds = grabber.getLengthInTime();
    //        return durationMicroseconds / 1000; // 微秒转毫秒
    //    } catch (FrameGrabber.Exception e) {
    //        throw new RuntimeException(e);
    //    }
    //}
    public static int getAudioDurationMs(String filePath) {
        // 强制注册所有FFmpeg编解码器（确保silk格式支持）
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

        String silkFilePath = "/home/alex/github/wechat-bot/data/audio/d01d4b48-fe79-4cca-a237-a6cfb2d535cc.silk";
        System.out.println("音频时长: " + getAudioDurationMs(silkFilePath) + " ms");
    }

}
