package com.wechat.bot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.service.MessageService;
import com.wechat.task.TaskQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author Alex
 * @since 2025/1/24 16:36
 * <p></p>
 */
@Slf4j
@RestController
@RequestMapping("/v2/api")
public class MessageController {


    @Resource
    private MessageService messageService;

    //@Resource
    private TaskQueue taskQueue;

    @PostMapping("/callback/collect")
    public void receiveMessages(@RequestBody String requestBody) {

        // 直接添加到任务中，单个线程去处理，增加，同时处理的数量


        Boolean filterOther = messageService.filterErrorMessage(requestBody);
        if (filterOther) {
            return;
        }
        messageService.receiveMsg(JSONObject.parseObject(requestBody));



    }


    @GetMapping("/callback/collect")
    public JSONObject collectGet() {

        return null;

    }

}
