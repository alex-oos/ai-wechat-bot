package com.wechat.bot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.bot.entity.dto.SystemConfigDto;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Auther: Alex
 * @since 2021/1/21 13:46
 * 
 */
//@Mapper
//@Repository // 也可以使用@Component，效果都是一样的，只是为了声明为bean
public interface SystemConfigMapper extends BaseMapper<SystemConfigDto> {




}
