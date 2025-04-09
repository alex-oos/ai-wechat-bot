package com.wechat.schedule;

import com.wechat.ai.session.SessionManager;
import com.wechat.bot.service.MsgTypeManageService;
import com.wechat.bot.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Alex
 * @since 2025/1/27 16:47
 * <p></p>
 */
@Slf4j
@Component
public class SessionClearSchedule {

    @Resource
    SessionService sessionService;

    @Resource
    MsgTypeManageService chatTypeManage;

    @Async
    @Scheduled(cron = "0 */10 * * * ?")
    //@Scheduled(fixedRate = 10 * 60 * 1000) // 每10分钟清理一次
    public void clearExpiredSessions() {

        SessionManager persionSessionManager = sessionService.getPersionSessionManager();
        SessionManager groupSessionManager = sessionService.getGroupSessionManager();
        List<String> personUserIds = persionSessionManager.clearExpiredSessions();
        List<String> groupUserIds = groupSessionManager.clearExpiredSessions();
        personUserIds.addAll(groupUserIds);
        if (personUserIds.isEmpty()) {
            return;
        }
        chatTypeManage.clear(personUserIds);





    }

}
