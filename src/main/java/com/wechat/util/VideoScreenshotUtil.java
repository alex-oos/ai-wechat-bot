package com.wechat.util;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

/**
 * @author Alex
 * @since 2025/3/21 11:00
 * <p>
 * 视频截图工具类
 * </p>
 */
@Slf4j
public class VideoScreenshotUtil {

    /**
     * 视频截图工具类：
     * 使用工具：FFmpeg
     * 使用教程：
     * Windows：从 FFmpeg官网（https://ffmpeg.org/download.html） 下载并配置环境变量。
     * Mac：用 Homebrew 安装：brew install ffmpeg
     * Linux：sudo apt-get install ffmpeg（Debian系）或 sudo yum install ffmpeg（RedHat系）
     */
    public static void useFFmpeq(String videoUrl, String imagePath) {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("ffmpeg", "-ss", "00:00:01", "-i", videoUrl, "-frames:v", "1", "-q:v", "2", imagePath);
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("视频截图成功");
            } else {
                System.out.println("视频截图失败,错误码" + exitCode);

                // 输出 FFmpeg 的错误信息（调试用）
                String errorOutput = new String(process.getInputStream().readAllBytes());
                System.err.println("FFmpeg 错误输出:\n" + errorOutput);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 使用javacv 这个不需要安装任何东西，增加java依赖包即可，目前使用这个
     * @param videoUrl
     * @param imagePath
     */
    public static void useJavacv(String videoUrl, String imagePath) {

        double targetTime = 1.0; // 第 1 秒

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoUrl)) {
            grabber.start();

            // 计算目标时间对应的帧位置（单位：微秒）
            grabber.setTimestamp((long) (targetTime * 1000000));

            // 读取当前帧
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage image = converter.convert(grabber.grabImage());

            if (image != null) {
                ImageIO.write(image, "jpg", new File(imagePath));
                System.out.println("截图成功！保存至: " + imagePath);
            } else {
                System.out.println("未找到指定时间的帧。");
            }
        } catch (Exception e) {
            System.err.println("截图失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        String videoPath = "https://dashscope-result-wlcb.oss-cn-wulanchabu.aliyuncs.com/1d/52/20250321/44cdee5d/b08c7184-fbd2-4952-b2f5-61b1e1013e58.mp4?Expires=1742610468&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=lmej1wRW5FDBAjZ%2FmNaWG66IdMc%3D";
        String imagePath = Path.of("image.jpg").toString();
        //useFFmpeq(videoPath, imagePath);
        useJavacv(videoPath, imagePath);
    }

}
