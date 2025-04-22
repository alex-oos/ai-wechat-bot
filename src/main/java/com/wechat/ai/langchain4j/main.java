//package com.wechat.ai.langchain4j;
//
//import com.wechat.ai.config.AiConfig;
//import dev.langchain4j.data.message.AiMessage;
//import dev.langchain4j.data.message.ChatMessage;
//import dev.langchain4j.data.message.SystemMessage;
//import dev.langchain4j.data.message.UserMessage;
//import dev.langchain4j.http.client.jdk.JdkHttpClientBuilderFactory;
//import dev.langchain4j.model.chat.response.ChatResponse;
//import dev.langchain4j.model.openai.OpenAiChatModel;
//
//import java.util.ArrayList;
//import java.util.Scanner;
//
/// **
// * @author Alex
// * @since 2025/4/21 16:16
// * <p></p>
// */
//public class main {
//
//    public static void main(String[] args) {
//        ArrayList<ChatMessage> list = new ArrayList<>();
//
//        SystemMessage systemmessage = SystemMessage.systemMessage("你好我是一个AI 助理");
//        list.add(systemmessage);
//        OpenAiChatModel demo = OpenAiChatModel.builder()
//                .apiKey(AiConfig.botConfig.getDashscopeApiKey())
//                .modelName("qwen-plus")
//                .temperature(0.7)
//                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
//                .httpClientBuilder(new JdkHttpClientBuilderFactory().create())
//                .build();
//
//        while (true) {
//            System.out.println("请输入你想要的内容");
//            Scanner sc = new Scanner(System.in);
//            String content = sc.nextLine();
//            list.add(UserMessage.from(content));
//            System.out.println("用户输入为：" + content);
//
//            ChatResponse chat = demo.chat(list);
//            AiMessage aiMessage = chat.aiMessage();
//            list.add(aiMessage);
//            System.out.println("AI：" + aiMessage.text());
//        }
//    }
//
//
//
//
//}
