package com.wechat.bot.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

/**
 * @author Alex
 * @since 2025/3/26 10:43
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName(value = "ai_system_prompt")
public class AiSystemPromptDTO {

    // 实现自增，必须数据库中设置自增
    @TableId(value = "id", type = IdType.AUTO) // 数据库ID自增，依赖于数据库。在插入操作生成SQL语句时，不会插入主键这一列
    private Long id;

    private String roleName;

    private String roleType;

    private String content;

    @TableField(value = "create_time", fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE, jdbcType = JdbcType.VARCHAR)
    private String updateTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;


}
