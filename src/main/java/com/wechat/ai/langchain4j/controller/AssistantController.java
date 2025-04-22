package com.wechat.ai.langchain4j.controller;

import com.wechat.ai.langchain4j.service.ChatAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alex
 * @since 2025/4/22 15:12
 * <p></p>
 */
@RestController
public class AssistantController {

    @Autowired
    private ChatAssistant chatAssistant;
    //private final StreamingAssistant streamingAssistant;

    //public AssistantController(ChatAssistant chatAssistant) {
    //    this.assistant = chatAssistant;
    //    //this.streamingAssistant = streamingAssistant;
    //}

    @GetMapping("/assistant")
    public String assistant(@RequestParam(value = "message", defaultValue = "What is the current time?") String message) {

        return chatAssistant.chat(message);
    }

    //@GetMapping(value = "/streamingAssistant", produces = TEXT_EVENT_STREAM_VALUE)
    //public Flux<String> streamingAssistant(
    //        @RequestParam(value = "message", defaultValue = "What is the current time?") String message) {
    //    return streamingAssistant.chat(message);
    //}
}
