package com.wechat.bot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.bot.entity.dto.ChatRoomsDTO;
import com.wechat.bot.mapper.ChatRoomMapper;
import com.wechat.bot.service.ChatRoomService;
import org.springframework.stereotype.Service;

/**
 * @author Alex
 * @since 2025/1/27 22:19
 * <p></p>
 */
@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomsDTO> implements ChatRoomService {

}
