package com.wechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Alex
 * @since 2025/1/27 00:00
 * <p>
 * 自定义异步线程池，为了更快进行处理
 * </p>
 */

@EnableAsync
@Configuration
public class AsyncConfig {


    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = CPU_COUNT * 2;

    private static final int MAX_POOL_SIZE = CPU_COUNT * 4;


    @Bean
    public TaskExecutor taskExecutor() {
        // 采用spring 中的线程池配置，优点是比自己配置线程池更简单
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(CORE_POOL_SIZE);
        // 设置最大线程数
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        // 设置队列
        executor.setQueueCapacity(100);
        // 设置线程池名称
        executor.setThreadNamePrefix("async-");
        // 线程之间传递对象
        executor.setTaskDecorator(null);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAwaitTerminationSeconds(300); // 手动设置等待时间,等待300s
        executor.setWaitForTasksToCompleteOnShutdown(true); // 等待任务完成再关闭
        executor.initialize();
        return executor;
    }

}
