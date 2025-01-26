package com.wechat.bot.ali.service.impl;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

/**
 * @author Alex
 * @since 2025/1/26 18:03
 * <p></p>
 */
public interface AliService {

    String textToText(String content);

    GenerationResult callWithMessage(String content) throws ApiException, NoApiKeyException, InputRequiredException;

}
