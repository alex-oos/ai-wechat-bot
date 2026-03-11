package com.wechat.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "admin.auth")
public class AdminAuthProperties {

    /**
     * 简单后台登录账号（用于管理端页面调用后端 API）
     */
    private String username = "admin";

    /**
     * 简单后台登录密码（建议通过环境变量覆盖）
     */
    private String password = "123456";

    /**
     * token 有效期（秒）
     */
    private long tokenTtlSeconds = 86400;
}
