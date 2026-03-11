package com.wechat.admin.service;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class AdminUserInitializer {

    @Bean
    @Order(1)
    public ApplicationRunner adminUserInitRunner(AdminUserService adminUserService) {
        return args -> adminUserService.ensureDefaultUser();
    }
}
