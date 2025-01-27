package com.wechat.bot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.ai.ali.service.impl.AliService;
import com.wechat.bot.service.CallBackService;
import com.wechat.config.SystemConfig;
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
@RestController()
@RequestMapping("/v2/api")
public class CallBackController {

/*     @Resource
    private TaskQueue messageQueue; */

    @Resource
    private SystemConfig systemConfig;

    @Resource
    private AliService aliService;

    @Resource
    private CallBackService callBackService;

    @PostMapping("/callback/collect")
    public void receiveMessages(@RequestBody String requestBody) {

        log.info("收到消息：{}", requestBody);
        JSONObject request = JSONObject.parseObject(requestBody);
        boolean isContain = request.containsKey("Appid");
        if (isContain) {
            callBackService.receiveMsg(request);
        }

    }


    @GetMapping("/callback/collect")
    public JSONObject collectGet() {

        return null;

    }

}
