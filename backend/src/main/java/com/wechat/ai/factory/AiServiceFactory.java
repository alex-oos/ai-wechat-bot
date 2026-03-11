package com.wechat.ai.factory;

import com.wechat.ai.enums.AiEnum;
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
 * <p>
 * 工厂模式，启动的时候，自动将其注入到spring 容器中，方便后续使用
 * </p>
 */
@Component
public class AiServiceFactory implements ApplicationContextAware, InitializingBean {

    private static final Map<AiEnum, AIService> aiserviceMap = new HashMap<>();
    private static final Map<String, AIService> aiTypeServiceMap = new HashMap<>();

    private ApplicationContext applicationContext;

    private static void register(AiEnum aiEnum, AIService aiService) {

        if (aiEnum != null && aiService != null) {
            if (aiserviceMap.containsKey(aiEnum)) {
                throw new RuntimeException(String.format("重复注册，aiEnum=%s, aiService=%s", aiEnum, aiService));
            }
        }
        aiserviceMap.put(aiEnum, aiService);
    }

    private static void registerAiType(String aiType, AIService aiService) {
        if (aiType == null || aiType.isBlank() || aiService == null) {
            return;
        }
        if (aiTypeServiceMap.containsKey(aiType)) {
            throw new RuntimeException(String.format("重复注册，aiType=%s, aiService=%s", aiType, aiService));
        }
        aiTypeServiceMap.put(aiType, aiService);
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

    public static AIService getAiServiceByType(String aiType) {
        if (aiType == null || aiType.isBlank()) {
            throw new RuntimeException("aiType不能为空");
        }
        AIService byType = aiTypeServiceMap.get(aiType);
        if (byType != null) {
            return byType;
        }
        AiEnum aiEnum = AiEnum.getByBotType(aiType);
        if (aiEnum != null) {
            return getAiService(aiEnum);
        }
        throw new RuntimeException(String.format("未找到 aiType=%s 的 AIService", aiType));
    }

    /**
     * 初始化的时候，将其注入进入
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String, AbstractAiService> beansOfType = applicationContext.getBeansOfType(AbstractAiService.class);

        for (AbstractAiService value : beansOfType.values()) {
            register(value.getAiEnum(), value);
            for (String aiType : value.supportedAiTypes()) {
                registerAiType(aiType, value);
            }
        }


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        if (this.applicationContext == null) {
            this.applicationContext = applicationContext;
        }

    }

}
