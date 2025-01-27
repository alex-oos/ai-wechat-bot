package com.wechat.bot.ai.contant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alex
 * @since 2025/1/27 12:05
 * <p></p>
 */

@AllArgsConstructor
@Getter
public enum AiEnum {

    ALI(1, "ali");


    private final int id;

    private final String name;


    public static AiEnum getById(int id) {

        for (AiEnum aiEnum : AiEnum.values()) {
            if (aiEnum.getId() == id) {
                return aiEnum;
            }
        }
        return null;
    }

    public static AiEnum getByName(String name) {

        for (AiEnum aiEnum : AiEnum.values()) {
            if (aiEnum.getName().equals(name)) {
                return aiEnum;
            }
        }
        return null;
    }


}
