package com.wechat.bot.config;

import com.wechat.bot.util.FileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alex
 * @since 2025/1/26 18:50
 * <p></p>
 */
@Configuration
public class Myconfig {


    @Bean
    public SystemConfig systemConfiguration() {

        return FileUtil.readFile();
    }

}
