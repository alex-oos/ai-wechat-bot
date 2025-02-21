package com.wechat.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Alex
 * @since 2025/1/27 16:47
 * <p></p>
 */
@Slf4j
@Component
public class ScheduledTask {

    //@Resource
    //MessageService callBackService;


    @Scheduled(cron ="")
    public void scheduled() {
        //TODO
        System.out.println("定时任务");
        //callBackService.groupMsg(null);

    }

}
