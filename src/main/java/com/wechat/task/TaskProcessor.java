package com.wechat.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/1/25 19:02
 * <p>
 *     任务管理器
 * </p>
 */
@Slf4j
@Component
public class TaskProcessor {

    @Resource
    private TaskQueue taskQueue;

    @Resource
    private TaskExecutor threadPool;


    public void processTasks() {
        // 1. 每个线程的线程任务死循环
        // 2, 这里触发的是提交任务到线程池中执行
        //3. 控制一下,提交多少个任务到线程池中
        int consumerThreadCount = 10;

        for (int i = 0; i < consumerThreadCount; i++) {
            threadPool.execute(() -> {
                while (true) {
                    try {
                        Runnable task = taskQueue.dequeue();
                        if (task == null) {
                            continue;
                        }
                        task.run();
                    } catch (Exception e) {
                        log.error("线程执行失败",e.getMessage());
                        //e.printStackTrace();
                    }
                }
            });

        }


    }


}
