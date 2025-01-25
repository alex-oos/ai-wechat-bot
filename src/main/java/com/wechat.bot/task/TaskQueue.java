package com.wechat.bot.task;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Alex
 * @since 2025/1/25 19:29
 * <p></p>
 */

@Component
public class TaskQueue {

    /**
     * 任务队列
     */
    BlockingQueue<Task> taskQueue = new LinkedBlockingDeque<>();


    public void offer(Task task) {

        taskQueue.offer(task);
    }

    public Task take() {

        try {
            return taskQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
