package com.wechat.bot.ali.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Alex
 * @since 2025/1/26 17:55
 * <p></p>
 */
@Data
@Component
@Configurable
public class QwenConfig {


    private String model = "qwen-plus";

    private String apiKey = "sk-4d61e913e8cf4185a816275dae53309b";


}
