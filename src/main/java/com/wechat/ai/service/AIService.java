package com.wechat.ai.service;

import java.util.List;

/**
 * @author Alex
 * @since 2025/1/27 01:20
 * <p></p>
 */
public interface AIService {


    List<String> textToText(String content);

    String textToImage(String content);

    String imageToText(String content);

    String imageToImage(String content);

    String imageToImage(String content, String style);

    String imageToImage(String content, String style, String prompt);

    String imageToImage(String content, String style, String prompt, String negativePrompt);

    Boolean checkIsEnabled();


}
