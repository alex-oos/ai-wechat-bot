package com.wechat.ai.xunfei.service;

import com.wechat.ai.enums.AiEnum;
import com.wechat.ai.service.AbstractAiService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex
 * @since 2025/1/27 15:54
 * <p></p>
 */
@Slf4j
//@Service
public class XunFeiService extends AbstractAiService {

    public XunFeiService() {

        super(AiEnum.DEEPSEEK);
    }

}
