package com.wechat.bot.task;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Alex
 * @since 2025/1/25 19:29
 * <p></p>
 */

@Component
public class TaskQueue {

    private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

    public void enqueue(Task task) throws InterruptedException {

        queue.put(task);
    }

    public Task dequeue() throws InterruptedException {

        return queue.take();
    }

    public int size() {

        return queue.size();
    }

    public void clear() {

        queue.clear();
    }

    public boolean isEmpty() {

        return queue.isEmpty();
    }


}
