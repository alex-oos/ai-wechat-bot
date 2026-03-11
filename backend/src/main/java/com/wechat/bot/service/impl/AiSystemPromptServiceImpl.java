package com.wechat.bot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.ai.session.Session;
import com.wechat.bot.entity.dto.AiSystemPromptDTO;
import com.wechat.bot.mapper.AiSystemPromptMapper;
import com.wechat.bot.service.AiSystemPromptService;
import org.springframework.stereotype.Service;

/**
 * @author Alex
 * @since 2025/3/26 10:54
 * <p></p>
 */
@Service
public class AiSystemPromptServiceImpl extends ServiceImpl<AiSystemPromptMapper, AiSystemPromptDTO> implements AiSystemPromptService {


    @Override
    public Boolean updateAiSystemPrompt(String content, Session session) {

        if (content != null && !content.isEmpty() && session != null && content.startsWith("#")) {
            content = content.replace("#", "");
            LambdaQueryWrapper<AiSystemPromptDTO> queryWrapper = new QueryWrapper<AiSystemPromptDTO>().lambda();
            queryWrapper.eq(AiSystemPromptDTO::getRoleName, content);
            AiSystemPromptDTO aiSystemPromptDTO = this.getOne(queryWrapper);
            if (aiSystemPromptDTO == null) {
                return false;
            }
            session.setSystemPrompt(aiSystemPromptDTO.getContent());
            return true;
        }

        return false;
    }


}
