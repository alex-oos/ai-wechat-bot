package com.wechat.schedule;

import com.wechat.ai.session.SessionManager;
import com.wechat.bot.service.MsgSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/1/27 16:47
 * <p></p>
 */
@Slf4j
@Component
public class ScheduledTaskSessionClear {

    @Resource
    MsgSourceService msgSourceService;

    @Async
    @Scheduled(fixedRate = 10 * 60 * 1000) // 每10分钟清理一次
    public void clearExpiredSessions() {

        SessionManager sessionMessage = msgSourceService.getSessionMessage();
        sessionMessage.clearExpiredSessions();
        log.info("清理过期会话");

    }

}
