package com.wechat.bot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.bot.entity.dto.TimedTaskDTO;
import com.wechat.bot.mapper.TimedTaskMapper;
import com.wechat.bot.service.TimedTaskService;
import org.springframework.stereotype.Service;

/**
 * @author Alex
 * @since 2025/3/26 15:13
 * <p></p>
 */
@Service
public class TimedTaskServiceImpl extends ServiceImpl<TimedTaskMapper, TimedTaskDTO> implements TimedTaskService {

}
