package com.wechat.bot.util;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.wechat.bot.config.UserInfoConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Alex
 * @since 2025/1/25 16:05
 * <p></p>
 */
public class FileUtil {

    public static UserInfoConfig readFile(String filePath) {

        String content = null;
        try {
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            content = new String(bytes);
            return JSONObject.parseObject(content, UserInfoConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFile(UserInfoConfig userInfoConfig, String filePath) {

        String content = JSONObject.toJSONString(userInfoConfig, JSONWriter.Feature.PrettyFormat);
        File file = new File(filePath);
        try {
            Files.write(file.toPath(), content.getBytes());
            System.out.println("文件写入成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
