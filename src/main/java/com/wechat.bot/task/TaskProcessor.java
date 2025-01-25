package com.wechat.bot.task;

import jakarta.annotation.Resource;
import org.apache.tomcat.util.threads.TaskQueue;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Alex
 * @since 2025/1/25 19:02
 * <p></p>
 */
@Component
public class TaskProcessor {

    @Resource
    private TaskQueue taskQueue;

    @Resource
    private ThreadPoolTaskExecutor threadPool;


    public void consumeTask() {

        //获取消息，然后进行回复
        while (true) {
            try {
                Task taskPo = taskQueue.take();
                //线程池去提交任务
                threadPool.execute(taskPo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


}
