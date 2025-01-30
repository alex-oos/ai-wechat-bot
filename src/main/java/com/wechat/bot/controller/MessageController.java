package com.wechat.bot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.ali.service.impl.AliService;
import com.wechat.bot.service.MessageService;
//import com.wechat.bot.task.Task;
//import com.wechat.bot.task.TaskQueue;
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

    @PostMapping("/callback/collect")
    public void receiveMessages(@RequestBody String requestBody) {

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
