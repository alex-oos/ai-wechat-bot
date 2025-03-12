package com.wechat.ai.service;

import com.wechat.ai.session.Session;

import java.util.Map;

/**
 * @author Alex
 * @since 2025/1/27 01:20
 * <p></p>
 */
public interface AIService {


    String textToText(Session session);

     Map<String, String> textToImage(String content);

    String imageToText(String content);

    String imageToImage(String content);

    String imageToImage(String content, String style);

    String imageToImage(String content, String style, String prompt);

    String imageToImage(String content, String style, String prompt, String negativePrompt);


}
