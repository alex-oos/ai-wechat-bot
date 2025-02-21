package com.wechat.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alex
 * @since 2025/1/27 12:05
 * <p></p>
 */

@AllArgsConstructor
@Getter
public enum AiEnum {

    ALI(1, "ali", List.of("qwen-plus", "qwen-max", "qwen-turbo", "qwen-max-lite", "qwen-max-pro", "qwen-max-lite-v2", "qwen-max-pro-v2", "qwen-max-lite-v3", "qwen-max-pro-v3", "qwen-max-l")),

    DEEPSEEK(2, "deepseek", List.of("deepseek-r1"));

    private final int id;

    private final String aiType;

    private final List<String> model;

    private static final Map<Integer, AiEnum> idMap = Arrays.stream(values())
            .collect(Collectors.toMap(AiEnum::getId, e -> e));

    private static final Map<String, AiEnum> aiTypeMap = Arrays.stream(values())
            .collect(Collectors.toMap(AiEnum::getAiType, e -> e));

    private static final Map<String, AiEnum> modelMap = Arrays.stream(values())
            .flatMap(e -> e.getModel().stream().map(model -> Map.entry(model, e)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static AiEnum getById(int id) {
        return idMap.get(id);
    }

    public static AiEnum getByBotType(String botType) {
        return aiTypeMap.get(botType);
    }

    public static AiEnum getByModel(String model) {
        return modelMap.get(model);
    }
}
