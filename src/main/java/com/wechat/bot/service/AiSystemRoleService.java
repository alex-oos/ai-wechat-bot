package com.wechat.bot.service;

import com.wechat.bot.entity.ChatMessage;

/**
 * @author Alex
 * @since 2025/3/25 23:34
 * <p></p>
 */
public interface AiSystemRoleService {

    void updatePersonSystemRole(String userId, String userName);
    void updateGroupSystemRole(String userId, String userName);

    /**
     * 判断是否切换系统角色
     * @param chatMessage
     * @param msgSource
     * @return
     */
    Boolean isUpdateAiSystemRole(String content);

}
