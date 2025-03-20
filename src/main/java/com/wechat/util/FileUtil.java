package com.wechat.util;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.wechat.bot.entity.BotConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Alex
 * @since 2025/1/25 16:05
 * <p></p>
 */
@Slf4j
public class FileUtil {


    /**
     * config 文件默认路径
     */
    public static Path configFilePath = Path.of("src/main/resources/static/config.json");

    public static BotConfig readFile() {

        if (!configFilePath.toFile().exists()) {
            return null;
        }

        String content = readFileText(configFilePath.toString());

        return JSONObject.parseObject(content, BotConfig.class);

    }

    public static void writeFile(BotConfig systemConfig) {

        checkFile();
        String content = JSONObject.toJSONString(systemConfig, JSONWriter.Feature.PrettyFormat);
        try {
            Files.write(configFilePath, content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static boolean checkFile() {

        if (!configFilePath.toFile().exists()) {

            try {
                configFilePath.toFile().createNewFile();
            } catch (IOException e) {
                log.error("文件创建失败", e);
                throw new RuntimeException("文件创建失败！", e);
            }
        }
        return true;
    }

    public static String readUseTxt() {

        return readFileText("docs/use.txt");


    }

    public static String readCronTxt() {
        return readFileText("docs/systemPrompt/cron.txt");

    }

    public static String readFileText(String filePath) {

        try {
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败，请检查config 文件是否存在");
        }
    }

    public static void main(String[] args) {

        String s = readUseTxt();
        System.out.println(s);
    }

}
