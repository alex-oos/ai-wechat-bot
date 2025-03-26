package com.wechat.bot.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Alex
 * @since 2025/1/27 22:16
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data

@TableName(value = "chat_rooms")
public class ChatRoomsDTO implements Serializable {

    // 实现自增，必须数据库中设置自增
    @TableId(value = "id", type = IdType.AUTO) // 数据库ID自增，依赖于数据库。在插入操作生成SQL语句时，不会插入主键这一列
    private Long id;

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


}
