package com.wechat.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 * @since 2025/1/30 16:45
 * <p></p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String userName; // 用户名

    private String nickName; // 昵称

    private String pyInitial; // 全拼首字母

    private String quanPin; // 全拼

    private Integer sex; // 性别 (1表示男性，0表示女性，根据您的实际需求定义)

    private String remark; // 备注

    private String remarkPyInitial; // 备注全拼首字母

    private String remarkQuanPin; // 备注全拼

    private String signature; // 个性签名

    private String alias; // 别名

    private String snsBgImg; // 社交背景图片

    private String country; // 国家

    private String bigHeadImgUrl; // 大头像URL

    private String smallHeadImgUrl; // 小头像URL

    private String description; // 描述

    private String cardImgUrl; // 名片图片URL

    private Object labelList; // 标签列表

    private String province; // 省份

    private String city; // 城市

    private Object phoneNumList; // 手机号列表

}
