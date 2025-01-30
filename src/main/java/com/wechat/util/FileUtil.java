package com.wechat.util;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.wechat.config.BotConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Alex
 * @since 2025/1/25 16:05
 * <p></p>
 */
public class FileUtil {

    /**
     * config 文件默认路径
     */
    public static Path configFilePath = Path.of("src/main/resources/static/config.json");

    public static BotConfig readFile() {

        if (!configFilePath.toFile().exists()) {
            return null;
        }

        String content = null;
        try {
            byte[] bytes = Files.readAllBytes(configFilePath);
            content = new String(bytes);
            return JSONObject.parseObject(content, BotConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败，请检查config 文件是否存在");
        }
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
                throw new RuntimeException(e);
            }
        }
        return true;
    }

}
