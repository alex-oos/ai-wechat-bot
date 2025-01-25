package com.wechat.bot.config;

import com.wechat.bot.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

/**
 * @author Alex
 * @since 2025/1/24 16:21
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
//@Configurable
public class UserInfoConfig {

    /**
     * 这些信息需要存储到一个文件里面，或，不能放到内存中，不然一直都会重复登录
     * token：用户token
     */

    private String token;
    /**
     * 设备id
     */
    private String appId;

    @Bean
    public UserInfoConfig init() {

        return FileUtil.readFile("src/main/resources/static/config.json");
    }




}
