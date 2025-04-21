package com.wechat.ai.ali.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Alex
 * @since 2025/4/21 16:16
 * <p></p>
 */
public class main {

    public static void main(String[] args) {

        //String me = "你好！";
        //System.out.println("用户：" + me);
        ArrayList<ChatMessage> list = new ArrayList<>();

        SystemMessage systemmessage = SystemMessage.systemMessage("你好我是一个AI 助理");
        list.add(systemmessage);
        //UserMessage userMessage = UserMessage.from(me);
        //list.add(userMessage);
        OpenAiChatModel demo = OpenAiChatModel.builder()
                .apiKey("sk-83493f6b46b248c5a3a008dca639ac55")
                .modelName("qwen-plus")
                .temperature(0.7)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")

                .build();

        ChatMemory chatMemory = null;

        while (true) {
            System.out.println("请输入你想要的内容");
            Scanner sc = new Scanner(System.in);
            String content = sc.nextLine();
            list.add(UserMessage.from(content));
            System.out.println("用户输入为：" + content);
            Response<AiMessage> response = demo.generate(list);
            TokenUsage tokenUsage = response.tokenUsage();
            System.out.println("tokenUsage：" + tokenUsage);
            AiMessage aiMessage = response.content();
            list.add(aiMessage);
            System.out.println("AI：" + aiMessage.text());
        }
    }


}
