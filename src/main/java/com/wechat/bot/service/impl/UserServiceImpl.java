package com.wechat.bot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.bot.entity.dto.UserDto;
import com.wechat.bot.mapper.UserMapper;
import com.wechat.bot.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Auther: 唐凯泽
 * @since 2021/5/31 下午 9:56
 * 
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDto> implements UserService {

}
