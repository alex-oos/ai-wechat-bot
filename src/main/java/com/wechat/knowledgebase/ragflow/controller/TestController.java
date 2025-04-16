package com.wechat.knowledgebase.ragflow.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.knowledgebase.ragflow.entity.ReplyEntity;
import com.wechat.knowledgebase.ragflow.service.RagFlowService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/4/14 18:18
 * <p></p>
 */
//@RestController
//@Component // 添加这个注解
public class TestController implements ApplicationRunner {

    @Resource
    private RagFlowService chatService;

    @GetMapping("/test")
    public String test() {

        String result = String.valueOf(chatService.chatWithAssistant("你好", "40271fd840b440c7bd05be8d804b432f"));
        return result;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        ReplyEntity replyEntity = chatService.chatWithAssistant("你好", null);
        System.out.println(JSONObject.toJSONString(replyEntity));
        //return result;
    }

}
