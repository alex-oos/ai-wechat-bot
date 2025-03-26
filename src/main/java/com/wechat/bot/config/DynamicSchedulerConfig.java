package com.wechat.bot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wechat.bot.entity.dto.TimedTaskDTO;
import com.wechat.bot.service.TimedTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Alex
 * @since 2025/3/26 15:18
 * <p></p>
 */
@Slf4j
@Configuration
@EnableScheduling
public class DynamicSchedulerConfig implements SchedulingConfigurer {

    @Resource
    private TimedTaskService timedTaskService;

    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 线程池任务调度器
     * <p>
     * 支持注解方式，@Scheduled(cron = "0/5 * * * * ?")
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() / 3 + 1);
        scheduler.setThreadNamePrefix("TaskScheduler-");
        scheduler.setRemoveOnCancelPolicy(true);  // 保证能立刻丢弃运行中的任务
        taskScheduler = scheduler; // 获取 句柄，方便后期获取 future

        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {


        LambdaQueryWrapper<TimedTaskDTO> query = new QueryWrapper<TimedTaskDTO>().lambda();
        query.eq(TimedTaskDTO::getStatus, "active");

        List<TimedTaskDTO> activeTasks = timedTaskService.list(query);

        activeTasks.forEach(task -> {
            Runnable taskLogic = () -> executeTaskLogic(task); // 任务执行逻辑
            // 根据 cron 或 interval 选择触发方式
            if (task.getCronExpression() != null) {
                // 添加cron的表达式
                registrar.addCronTask(new CronTask(taskLogic, task.getCronExpression()));
            } else {
                long intervalMs = task.getIntervalTime() * 1000L;
                registrar.addFixedRateTask(new FixedRateTask(taskLogic, intervalMs, intervalMs));
            }
        });
        registrar.setTaskScheduler(taskScheduler);
    }

    private void executeTaskLogic(TimedTaskDTO task) {
        // 执行 SQL 或调用脚本（需实现具体逻辑）
        //jdbcTemplate.execute(task.getTaskSql());
        // 更新 last_execute_time 和 next_execute_time
        log.info("执行任务：{}", task.getTaskName());
    }


}
