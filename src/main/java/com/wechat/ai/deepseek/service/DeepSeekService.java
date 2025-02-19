package com.wechat.ai.deepseek.service;

import com.wechat.ai.contant.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Alex
 * @since 2025/2/19 18:52
 * <p></p>
 */
@Slf4j
@Service
public class DeepSeekService extends AbstractAiService  {

    public DeepSeekService() {

        super(AiEnum.DEEPSEEK);
    }



}
