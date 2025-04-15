package com.wechat.bot.controller;

import com.wechat.bot.config.DynamicSchedulerConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/4/15 15:22
 * <p></p>
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private DynamicSchedulerConfig dynamicSchedulerConfig;

    @GetMapping("/remove/{}")
    public String removeTask() {

        dynamicSchedulerConfig.removeTask(1L);
        return "test";
    }

    @GetMapping("/remove/all")
    public String removeAllTask() {

        dynamicSchedulerConfig.removeAllTask();
        return "success";
    }

}
