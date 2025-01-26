package com.wechat.bot.util;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.wechat.bot.config.SystemConfig;

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

    public static SystemConfig readFile() {

        String content = null;
        try {
            byte[] bytes = Files.readAllBytes(configFilePath);
            content = new String(bytes);
            return JSONObject.parseObject(content, SystemConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败，请检查config 文件是否存在");
        }
    }

    public static void writeFile(SystemConfig systemConfig) {

        String content = JSONObject.toJSONString(systemConfig, JSONWriter.Feature.PrettyFormat);
        try {
            Files.write(configFilePath, content.getBytes());
            System.out.println("文件写入成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
