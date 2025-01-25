package com.wechat.bot.task;

import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Alex
 * @since 2025/1/25 18:37
 * <p>
 * <p>
 * 启动的时候，先获取消息，然后进行回复
 * </p>
 */
@Component
public class BackGroudCommandLineRunner implements CommandLineRunner {

    @Resource
    private TaskProcessor taskProcessor;

    @Override
    public void run(String... strings) throws Exception {

        //获取消息，将消息放到队列之中，然后一个一个进行执行
        taskProcessor.consumeTask();
        // 然后再一个一个回复消息即可


    }

}
