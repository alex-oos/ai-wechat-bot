package com.wechat.bot.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;

/**
 * @author Alex
 * @since 2025/1/27 22:12
 * <p></p>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "friends")
public class FriendDTO implements Serializable {

    // 实现自增，必须数据库中设置自增
    @TableId(value = "id", type = IdType.AUTO) // 数据库ID自增，依赖于数据库。在插入操作生成SQL语句时，不会插入主键这一列
    private Integer id;

    private String userName;

    private String nickName;

    private String pyInitial;

    private String quanPin;

    private String sex;

    private String remark;

    private String remarkPyInitial;

    private String remarkQuanPin;

    private String signature;

    private String alias;

    private String snsBgImg;

    private String country;

    private String bigHeadImgUrl;

    private String smallHeadImgUrl;

    private String description;

    private String cardImgUrl;

    private String labelList;

    private String province;

    private String city;

    private String phoneNumList;

    @TableField(value = "create_time", fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String createTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;


}
