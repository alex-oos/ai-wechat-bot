package com.wechat.bot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.bot.entity.dto.FriendDTO;

/**
 * @author Alex
 * @since 2025/1/27 22:24
 * <p></p>
 */
public interface FriendService extends IService<FriendDTO> {

    /**
     * 同步通讯录
     */
    void syncContacts();



}
