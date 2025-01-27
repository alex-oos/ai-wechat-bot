package com.wechat.bot.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex
 * @since 2025/1/27 20:57
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "system_config")
public class UserDto implements Serializable {

    // 实现自增，必须数据库中设置自增
    @TableId(value = "id", type = IdType.AUTO) // 数据库ID自增，依赖于数据库。在插入操作生成SQL语句时，不会插入主键这一列
    private Long id;

    private String appId;

    private String token;

    /**
     * 自动填充，插入的时候，自动加入时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    // 自动填充，更新的时候自动加入时间
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

}
