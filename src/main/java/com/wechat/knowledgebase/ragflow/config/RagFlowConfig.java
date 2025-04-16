package com.wechat.knowledgebase.ragflow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alex
 * @since 2025/4/14 17:30
 * <p></p>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("knowledge.ragflow.config")
public class RagFlowConfig {

    private String host;

    private String apiKey;

    private String rebootId;


}
