package com.wechat.util;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.wechat.bot.entity.BotConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Alex
 * @since 2025/1/25 16:05
 * <p></p>
 */
@Slf4j
public class FileUtil {


    // 外部配置文件路径（如：工作目录/config.json）
    private static final String EXTERNAL_CONFIG_PATH = "config.json";

    // 资源中的默认配置路径（需与resources目录下的路径一致）
    private static final String DEFAULT_CONFIG_RESOURCE = "static/config.json";

    /**
     * config 文件默认路径
     */
    private static final Path configFilePath = Path.of("src/main/resources/static/config.json");

    public static BotConfig readFile() {


        String content = readFileText(getConfigFilePath().toString());

        return JSONObject.parseObject(content, BotConfig.class);

    }

    public static String readFileText(String filePath) {

        try {
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(String.format("文件读取失败，请检查%s文件是否存在", filePath));
        }
    }

    public static Path getConfigFilePath() {

        Path externalPath = Paths.get(EXTERNAL_CONFIG_PATH);
        String env = "local";

        // 如果外部文件已存在，直接返回
        if (Files.exists(externalPath)) {
            return externalPath;
        }

        // 仅在 Windows 环境下自动生成默认配置
        if (env.equals("exe")) {
            try (InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_RESOURCE)) {
                if (is == null) {
                    throw new FileNotFoundException("默认配置文件未找到: " + DEFAULT_CONFIG_RESOURCE);
                }
                // 复制到外部路径
                Files.copy(is, externalPath, StandardCopyOption.REPLACE_EXISTING);
                //System.out.println("已生成默认配置文件: " + externalPath.toAbsolutePath());
                return externalPath;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 非 Windows 系统直接返回路径（不自动创建文件）
            URL url = FileUtil.class.getClassLoader().getResource(DEFAULT_CONFIG_RESOURCE);
            try {
                return Path.of(url.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeFile(BotConfig systemConfig) {

        checkFile();
        String content = JSONObject.toJSONString(systemConfig, JSONWriter.Feature.PrettyFormat);
        try {
            Files.write(getConfigFilePath(), content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static boolean checkFile() {

        if (!getConfigFilePath().toFile().exists()) {

            try {
                getConfigFilePath().toFile().createNewFile();
            } catch (IOException e) {
                log.error("文件创建失败", e);
                throw new RuntimeException("文件创建失败！", e);
            }
        }
        return true;
    }

    /**
     * 阅读说明书
     *
     * @return
     */
    public static String readUseManual() {

        return readFileText(Path.of("docs", "usermanual", "version2.txt").toString());


    }

    public static String readCronTxt() {

        return readFileText(Path.of("docs", "systemPrompt", "cron.txt").toString());

    }

    public static void main(String[] args) {

    }

}
