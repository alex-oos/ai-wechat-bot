package com.wechat.ai.contant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author Alex
 * @since 2025/1/27 12:05
 * <p></p>
 */

@AllArgsConstructor
@Getter
public enum AiEnum {

    ALI(1, "ali", List.of("qwen-plus", "qwen-max", "qwen-turbo", "qwen-max-lite", "qwen-max-pro", "qwen-max-lite-v2", "qwen-max-pro-v2", "qwen-max-lite-v3", "qwen-max-pro-v3", "qwen-max-l","deepseek-r1")),

    DEEPSEEK(2, "deepseek", List.of("qwen-plus", "qwen-max", "qwen-turbo", "qwen-max-lite", "qwen-max-pro", "qwen-max-lite-v2", "qwen-max-pro-v2", "qwen-max-lite-v3", "qwen-max-pro-v3", "qwen-max-l"));


    private final int id;

    private final String aiType;

    private final List<String> model;


    public static AiEnum getById(int id) {

        for (AiEnum aiEnum : AiEnum.values()) {
            if (aiEnum.getId() == id) {
                return aiEnum;
            }
        }
        return null;
    }

    public static AiEnum getByBotType(String botType) {

        for (AiEnum aiEnum : AiEnum.values()) {
            if (aiEnum.getAiType().equals(botType)) {
                return aiEnum;
            }
        }
        return null;
    }

    public static AiEnum getByModel(String model) {

        for (AiEnum aiEnum : AiEnum.values()) {
            if (aiEnum.getModel().contains(model)) {
                return aiEnum;
            }
        }
        return null;
    }


}
