package com.wechat.bot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.dto.AiSystemPromptDTO;

/**
 * @author Alex
 * @since 2025/3/26 10:53
 * <p></p>
 */
public interface AiSystemPromptService extends IService<AiSystemPromptDTO> {

    /**
     * 更新AI系统提示语
     *
     * @param content
     * @param session
     * @return
     */
    Boolean updateAiSystemPrompt(String content, Session session);


}
