package com.wechat.bot.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

/**
 * @author Alex
 * @since 2026/3/11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName(value = "admin_users")
public class AdminUserDTO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String passwordHash;

    private String displayName;

    private String status;

    @TableField(value = "create_time", fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE, jdbcType = JdbcType.VARCHAR)
    private String updateTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
