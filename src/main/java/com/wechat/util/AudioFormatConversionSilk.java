package com.wechat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Alex
 * @since 2025/3/24 11:29
 * <p></p>
 */
public class AudioFormatConversionSilk {

    public static void convertToAudioFormat(String inputFilePath, String outputFilePath) {

        ProcessBuilder processBuilder = new ProcessBuilder();
        String command = "";
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            System.out.println("windows 下暂时不支持");
            return;
        } else {
            command = "./tool/linux/silkenc";
        }
        processBuilder.command(command, inputFilePath, outputFilePath, "-rate", "24000", "-tencent");
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("音频格式转换成功");
            } else {
                System.out.println("音频格式转换失败,错误码" + exitCode);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {

        convertToAudioFormat("/home/alex/github/wechat-bot/data/audio/20252403/ac55e314-0925-4238-a698-b0040b6566b0.pcm", "/home/alex/github/wechat-bot/data/audio/20252403/1.silk");
    }

}
