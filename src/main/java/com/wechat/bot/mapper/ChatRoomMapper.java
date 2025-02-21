package com.wechat.bot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.bot.entity.dto.ChatRoomsDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Alex
 * @since 2025/1/27 22:18
 * <p></p>
 */
//@Mapper
public interface ChatRoomMapper extends BaseMapper<ChatRoomsDto> {

}
