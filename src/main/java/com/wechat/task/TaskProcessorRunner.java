package com.wechat.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/1/25 18:37
 * <p>
 * <p>
 * 后台任务
 * 启动的时候，先获取消息，然后进行回复
 * </p>
 */
@Slf4j
//@Component
public class TaskProcessorRunner implements CommandLineRunner {

    @Resource
    private TaskProcessor taskProcessor;


    @Override
    public void run(String... strings) throws Exception {


        taskProcessor.processTasks(); // 启动任务处理器


    }

}
