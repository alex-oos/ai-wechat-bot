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

    String imageToText(Session session);

    Map<String, Object> textToVideo(String content);

    Integer textToVoice(String content, String audioPath);


}
