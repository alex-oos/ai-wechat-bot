package com.wechat.bot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.ali.service.impl.AliService;
import com.wechat.bot.bot.service.CallBackService;
import com.wechat.bot.config.SystemConfig;
import com.wechat.bot.gewechat.service.MessageApi;
import com.wechat.bot.task.Task;
import com.wechat.bot.task.TaskQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Alex
 * @since 2025/1/24 16:36
 * <p></p>
 */
@Slf4j
@RestController()
@RequestMapping("/v2/api")
public class CallBackController {

    @Resource
    private TaskQueue messageQueue;

    @Resource
    private SystemConfig systemConfig;

    @Resource
    private AliService aliService;

    @Resource
    private CallBackService callBackService;

    @PostMapping("/callback/collect")
    public void collect(@RequestBody JSONObject requestBody) {

        String appid = requestBody.getString("Appid");
        if (appid != null) {
            JSONObject data = requestBody.getJSONObject("Data");
            String fromUsername = data.getJSONObject("FromUserName").getString("string");
            String toUserName = data.getJSONObject("ToUserName").getString("string");
            String content = data.getJSONObject("Content").getString("string");
            String msgSource = data.getString("MsgSource");


            Boolean isFilter = callBackService.filterUser(fromUsername, toUserName, msgSource, content);
            if (isFilter) {
                return;
            }

            List<String> strings = aliService.textToText(content);

            strings.forEach(msg -> {

                MessageApi.postText(appid, fromUsername, msg, toUserName);

            });

            //发送消息
            //Task message = new Task(() -> {
            //    String replyContent = aliService.textToText(content);
            //    MessageApi.postText(appid, fromUsername, replyContent, toUserName);
            //});
            ////
            //try {
            //    messageQueue.enqueue(message);
            //} catch (InterruptedException e) {
            //    throw new RuntimeException(e);
            //}


        }

    }

    @GetMapping("/callback/collect")
    public JSONObject collectGet() {


        return null;

    }

}
