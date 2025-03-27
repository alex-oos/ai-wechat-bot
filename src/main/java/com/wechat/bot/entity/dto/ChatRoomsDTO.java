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
 * @since 2025/1/27 22:16
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName(value = "chat_rooms")
public class ChatRoomsDTO implements Serializable {

    // 实现自增，必须数据库中设置自增
    @TableId(value = "chat_room_id")
    private String chatRoomId;

    private String nickName;

    private String pyInitial;

    private String quanPin;

    private String sex;

    private String remark;

    private String remarkPyInitial;

    private String remarkQuanPin;

    private String signature;

    //private String chatRoomNotify;
    //
    //private String chatRoomOwner;
    //
    //private String smallHeadImgUrl;

    @TableField(value = "create_time", fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String createTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;


}
