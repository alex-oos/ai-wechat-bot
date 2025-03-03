package com.wechat.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Alex
 * @since 2025/1/25 19:29
 * <p></p>
 */

@Slf4j
@Component
public class TaskQueue {

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public void enqueue(Runnable task){

        try {
            queue.put(task);
        } catch (InterruptedException e) {
            log.error("异常：", e);
            //throw new RuntimeException(e);
        }
    }

    public Runnable dequeue() {

        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error("异常：", e);
            //throw new RuntimeException(e);
        }
        return null;
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
