package com.wechat.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Alex
 * @since 2025/1/25 18:45
 * <p></p>
 */
@Component
@Configurable
public class ThreadPoolConfig {


    public static int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    public static final int CORE_POOL_SIZE = CPU_COUNT * 2;

    public static final int MAX_POOL_SIZE = CPU_COUNT * 4;


    @Bean
    public ThreadPoolTaskExecutor threadPool() {

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setQueueCapacity(100);
        threadPoolTaskExecutor.setThreadNamePrefix("threadpool-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;


    }

}
