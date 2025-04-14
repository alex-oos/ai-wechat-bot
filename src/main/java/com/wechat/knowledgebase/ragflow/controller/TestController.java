package com.wechat.knowledgebase.ragflow.controller;

import com.wechat.knowledgebase.ragflow.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/4/14 18:18
 * <p></p>
 */
@RestController
public class TestController {

    @Resource
    private ChatService chatService;

    @GetMapping("/test")
    public String test() {

        String result = chatService.chatWithAssistant("你好", "40271fd840b440c7bd05be8d804b432f");
        return result;
    }

}
