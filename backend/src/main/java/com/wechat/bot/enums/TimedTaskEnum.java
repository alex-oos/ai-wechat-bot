package com.wechat.bot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alex
 * @since 2025/3/26 16:56
 * <p></p>
 */
@Getter
@AllArgsConstructor
public enum TimedTaskEnum {

    ACTIVE(1, "active", "激活"),
    PAUSED(2, "paused", "暂停");


    private Integer id;

    private String status;

    private String desc;


}
