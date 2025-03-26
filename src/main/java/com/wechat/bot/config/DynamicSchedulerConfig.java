package com.wechat.bot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wechat.bot.entity.dto.TimedTaskDTO;
import com.wechat.bot.enums.TimedTaskEnum;
import com.wechat.bot.service.TimedTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Alex
 * @since 2025/3/26 15:18
 * <p></p>
 */
@Slf4j
@Configuration
@EnableScheduling
public class DynamicSchedulerConfig implements SchedulingConfigurer {

    private final Map<Long, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();

    @Resource
    private TimedTaskService timedTaskService;

    private ScheduledTaskRegistrar taskRegistrar;

    /**
     * 线程池任务调度器
     * <p>
     * 支持注解方式，@Scheduled(cron = "0/5 * * * * ?")
     */
    // 自定义线程池配置
    @Bean()
    public TaskScheduler taskScheduler() {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("DynamicTask-");
        return scheduler;
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {

        this.taskRegistrar = registrar;
        this.taskRegistrar.setScheduler(taskScheduler()); // 自定义线程池
        refreshTasks(); // 初始化加载任务

    }


    // 刷新任务列表（从数据库加载）
    public void refreshTasks() {

        LambdaQueryWrapper<TimedTaskDTO> query = new QueryWrapper<TimedTaskDTO>().lambda();
        query.eq(TimedTaskDTO::getStatus, TimedTaskEnum.ACTIVE.getStatus());

        List<TimedTaskDTO> activeTasks = timedTaskService.list(query);
        activeTasks.forEach(this::addTask);
        //activeTasks.forEach(task -> {
        //    Runnable taskLogic = () -> executeTaskLogic(task); // 任务执行逻辑
        //    // 根据 cron 或 interval 选择触发方式
        //    if (task.getCronExpression() != null) {
        //        // 添加cron的表达式
        //        registrar.addCronTask(new CronTask(taskLogic, task.getCronExpression()));
        //    } else {
        //        long intervalMs = task.getIntervalTime() * 1000L;
        //        registrar.addFixedRateTask(new FixedRateTask(taskLogic, intervalMs, intervalMs));
        //    }
        //});
    }

    // 添加任务
    public void addTask(TimedTaskDTO task) {

        Runnable taskRunnable = () -> executeTaskLogic(task);
        Trigger trigger = triggerContext -> {
            CronTrigger cronTrigger = new CronTrigger(task.getCronExpression());
            return cronTrigger.nextExecutionTime(triggerContext);
        };

        ScheduledFuture<?> future = taskRegistrar.getScheduler().schedule(taskRunnable, trigger);
        taskMap.put(task.getId(), future);
    }

    // 移除/关闭任务
    public void removeTask(String taskId) {

        ScheduledFuture<?> future = taskMap.get(taskId);
        if (future != null) {
            future.cancel(true); // 终止任务执行
            taskMap.remove(taskId);
        }
        timedTaskService.removeById(taskId);
    }

    public void removeAllTask() {

        taskMap.clear();
        timedTaskService.remove(null);


    }

    private void executeTaskLogic(TimedTaskDTO task) {
        // 执行 SQL 或调用脚本（需实现具体逻辑）
        // 更新 last_execute_time 和 next_execute_time
        log.info("执行任务：{}", task.getTaskName());
        task.setLastExecuteTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        try {
            // TODO（定时任务执行
            // 定时去发送内容提醒
            log.info("定时任务执行成功");
            timedTaskService.saveOrUpdate(task);
        } catch (Exception e) {
            task.setStatus(TimedTaskEnum.PAUSED.getStatus());
            timedTaskService.saveOrUpdate(task);
            log.error("定时任务执行失败，错误原因为：{}", e.getMessage());
        }
    }


}
