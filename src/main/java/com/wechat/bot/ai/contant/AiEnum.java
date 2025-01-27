package com.wechat.bot.ai.contant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alex
 * @since 2025/1/27 12:05
 * <p></p>
 */

@AllArgsConstructor
@Getter
public enum AiEnum {

    ALI(1, "ali");


    private int id;

    private String desc;


    public static AiEnum getById(int id) {

        for (AiEnum aiEnum : AiEnum.values()) {
            if (aiEnum.getId() == id) {
                return aiEnum;
            }
        }
        return null;
    }


}
