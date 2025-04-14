package com.wechat.knowledgebase.ragflow.controller;

import com.wechat.knowledgebase.ragflow.service.ChatService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
    private ChatService chatService;

    @GetMapping("/test")
    public String test() {

        String result = chatService.chatWithAssistant("你好", "40271fd840b440c7bd05be8d804b432f");
        return result;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String result = chatService.chatWithAssistant("你好", "40271fd840b440c7bd05be8d804b432f");
        System.out.println(result);
        //return result;
    }

}
