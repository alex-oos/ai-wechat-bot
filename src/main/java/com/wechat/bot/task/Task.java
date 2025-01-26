package com.wechat.bot.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 * @since 2025/1/25 18:38
 * <p></p>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Task {

    private String taskName;


    public void run() {
        // 任务参数是什么？如何更新
        System.out.println("执行任务");

    }

}
