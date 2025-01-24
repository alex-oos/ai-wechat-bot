package com.wechat.bot.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
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


    @GetMapping("/callback/collect")
    public JSONObject collect() {

        JSONObject response = new JSONObject();
        response.put("ret", 200);
        response.put("msg", "操作成功");
        return response;
    }

}
