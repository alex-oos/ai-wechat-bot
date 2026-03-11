package com.wechat.bot.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("ai_providers")
public class AiProviderDTO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String aiType;

    private String model;

    private String apiBaseUrl;

    private String apiKey;

    private Integer enabled;

    @TableField(value = "create_time", fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE, jdbcType = JdbcType.VARCHAR)
    private String updateTime;
}
