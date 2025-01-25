package com.wechat.bot.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alex
 * @since 2025/1/24 16:36
 * <p></p>
 */
@RestController()
@RequestMapping("/v2/api")
public class CallBackController {

    @Resource
    private JSONObject response;

    @PostMapping("/callback/collect")
    public JSONObject collect() {

        return response;
    }

}
