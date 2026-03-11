package com.wechat.bot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.bot.entity.dto.ChatRoomsDTO;

import java.util.List;

/**
 * @author Alex
 * @since 2025/1/27 22:18
 * <p></p>
 */
public interface ChatRoomService extends IService<ChatRoomsDTO> {


    ChatRoomsDTO getChatRoomByRoomId(String roomId);


    void syncChatRooms(List<String> chatroomIds);
}
