package com.wechat.ai.factory;

import com.wechat.ai.contant.AiEnum;
import com.wechat.ai.service.AIService;
import com.wechat.ai.service.AbstractAiService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex
 * @since 2025/1/27 12:03
 * <p></p>
 */
@Component
public class AiServiceFactory implements ApplicationContextAware, InitializingBean {

    private static final Map<AiEnum, AIService> aiserviceMap = new HashMap<>();

    private ApplicationContext applicationContext;

    private static void register(AiEnum aiEnum, AIService aiService) {

        if (aiEnum != null && aiService != null) {
            if (aiserviceMap.containsKey(aiEnum)) {
                throw new RuntimeException(String.format("重复注册，aiEnum=%s, aiService=%s", aiEnum, aiService));
            }
        }
        aiserviceMap.put(aiEnum, aiService);
    }

    public static AIService getAiService(AiEnum aiEnum) {

        if (aiEnum == null) {
            throw new RuntimeException("aiEnum不能为空");
        }

        if (!aiserviceMap.containsKey(aiEnum)) {
            throw new RuntimeException(String.format("aiEnum=%s, aiService=%s", aiEnum, aiserviceMap.get(aiEnum)));
        }

        return aiserviceMap.get(aiEnum);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String, AbstractAiService> beansOfType = applicationContext.getBeansOfType(AbstractAiService.class);

        for (AbstractAiService value : beansOfType.values()) {
            register(value.getAiEnum(), value);
        }


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        if (this.applicationContext == null) {
            this.applicationContext = applicationContext;
        }

    }

}
