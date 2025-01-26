package com.wechat.bot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.ali.service.impl.AliService;
import com.wechat.bot.config.SystemConfig;
import com.wechat.bot.gewechat.service.MessageApi;
import com.wechat.bot.task.MessageQueue;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Alex
 * @since 2025/1/24 16:36
 * <p></p>
 */
@RestController()
@RequestMapping("/v2/api")
public class CallBackController {

    @Resource
    private MessageQueue taskQueue;

    @Resource
    private SystemConfig systemConfig;

    @Resource
    private AliService aliService;


    @PostMapping("/callback/collect")
    public JSONObject collect(@RequestBody JSONObject requestBody) {

        String appid = requestBody.getString("Appid");
        if (appid != null) {
            JSONObject data = requestBody.getJSONObject("Data");
            String fromUsername = data.getJSONObject("FromUserName").getString("string");
            String toUserName = data.getJSONObject("ToUserName").getString("string");
            String content = data.getJSONObject("Content").getString("string");
            // 请求Ai 大模型，获取回复

            String s = aliService.textToText(content);
            // 发送消息
            JSONObject ToUserName = MessageApi.postText(appid, fromUsername, "你好", toUserName);
        }


        System.out.println(requestBody);
        return null;
    }

    @GetMapping("/callback/collect")
    public JSONObject collectGet(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((k, v) -> {
            System.out.println(k + ":" + v[0]);
        });
        return null;

    }

}
