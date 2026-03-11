package com.wechat.bot.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

/**
 * @author Alex
 * @since 2025/3/26 15:10
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName(value = "timed_tasks")
public class TimedTaskDTO {

    // 实现自增，必须数据库中设置自增
    @TableId(value = "id", type = IdType.AUTO) // 数据库ID自增，依赖于数据库。在插入操作生成SQL语句时，不会插入主键这一列
    private Long id;

    private String taskName;

    private Integer intervalTime;

    private String cronExpression;

    private String lastExecuteTime;

    private String nextExecuteTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 如果是群，则需要记录群名，如果是个人，这里需要的是用户名
     */
    private String name;

    @TableField(value = "create_time", fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String createTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

}
