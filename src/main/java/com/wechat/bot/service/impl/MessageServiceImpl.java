package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.service.MessageService;
import com.wechat.bot.service.MsgSourceService;
import com.wechat.gewechat.service.ContactApi;
import com.wechat.task.TaskQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * @author Alex
 * @since 2025/1/26 20:00
 * <p></p>
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MsgSourceService msgSourceService;


    @Resource
    private BotConfig botConfig;

    @Resource
    private TaskQueue taskQueue;

    @Async
    @Override
    public void receiveMsg(JSONObject requestBody) {

        String appid = requestBody.getString("Appid");
        String wxid = requestBody.getString("Wxid");
        JSONObject data = requestBody.getJSONObject("Data");
        String fromUserName = data.getJSONObject("FromUserName").getString("string");
        String toUserName = data.getJSONObject("ToUserName").getString("string");
        String content = data.getJSONObject("Content").getString("string");
        String msgSource = data.getString("MsgSource");
        String msgId = data.getString("'NewMsgId'");
        // 消息类型
        Integer msgType = data.getInteger("MsgType");
        MsgTypeEnum msgTypeEnum = MsgTypeEnum.getMsgTypeEnum(msgType);
        // 判断消息类型，进行一系列的操作
        switch (msgTypeEnum) {
            case TEXT:
                break;
            case IMAGE:
                //图片保存一下
                // 提取图片缩略图的Base64并保存为文件
                String imgBuf = data.getJSONObject("ImgBuf").getString("buffer");
                byte[] imageBytes = Base64.getDecoder().decode(imgBuf);
                File imageFile = new File("thumbnail.jpg");
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    fos.write(imageBytes);
                    System.out.println("图片缩略图已保存为: " + imageFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case VOICE:
                break;
            default:
                break;

        }
        boolean isGroup = fromUserName.contains("@chatroom");
        ChatMessage chatMessage = ChatMessage.builder()
                .msgId(msgId)
                .createTime(data.getLong("CreateTime"))
                .ctype(MsgTypeEnum.getMsgTypeEnum(msgType))
                .content(content)
                .fromUserId(fromUserName)
                .toUserId(toUserName)
                .isMyMsg(wxid.equals(fromUserName))
                .isGroup(isGroup)
                .isAt(content.contains("@"))
                .actualUserId(wxid)
                .appId(appid)
                .rawMsg(requestBody)
                .build();

        // 过滤掉非用户信息
        Boolean isFilter = this.filterNotUserMessage(chatMessage, msgSource);
        if (isFilter) {
            return;
        }

        log.info("收到消息{}", chatMessage.getRawMsg());
        this.updateMsgType(chatMessage);
        // 获取好友的信息
        JSONObject briefInfo = ContactApi.getBriefInfo(appid, Collections.singletonList(chatMessage.getFromUserId()));
        if (briefInfo.getInteger("ret") == 200) {
            JSONArray dataList = briefInfo.getJSONArray("data");
            JSONObject userInfo = dataList.getJSONObject(0);
            String remark = userInfo.getString("remark");
            String nickname = remark != null ? remark : userInfo.getString("nickName");
            chatMessage.setFromUserNickname(nickname);
        }

        if (chatMessage.getIsGroup()) {
            log.info("群消息类型");
            // 多消息任务来处理，更快一些
            taskQueue.enqueue(() -> {
                msgSourceService.groupMsg(chatMessage);

            });
        } else {
            log.info("个人消息");
            taskQueue.enqueue(() -> {
                msgSourceService.personalMsg(chatMessage);

            });
            //msgSourceService.personalMsg(chatMessage);
        }


    }

    /**
     * 过滤掉一些消息
     * 检查消息是否来自非用户账号（如公众号、腾讯游戏、微信团队等）
     * <p>
     * Args:
     * msg_source: 消息的MsgSource字段内容
     * from_user_id: 消息发送者的ID
     * <p>
     * Returns:
     * bool: 如果是非用户消息返回True，否则返回False
     * <p>
     * Note:
     * 通过以下方式判断是否为非用户消息：
     * 1. 检查MsgSource中是否包含特定标签
     * 2. 检查发送者ID是否为特殊账号或以特定前缀开头
     *
     * @return true代表是过滤的消息，false代表不是过滤的消息
     */
    public Boolean filterNotUserMessage(ChatMessage chatMessage, String msgSource) {

        // 防止给自己发消息
        if (chatMessage.getIsMyMsg()) {
            return true;
        }
        ArrayList<String> list1 = new ArrayList<>();
        Collections.addAll(list1, "Tencent-Games", "weixin");
        //过滤公众号与腾讯团队
        if (list1.contains(chatMessage.getFromUserId()) || chatMessage.getFromUserId().startsWith("gh_")) {
            return true;
        }
        // 添加过滤标签
        List<String> list = new ArrayList<>();
        Collections.addAll(list, "<tips>3</tips>", "<bizmsgshowtype>", "</bizmsgshowtype>", "<bizmsgfromuser>", "</bizmsgfromuser>");
        if (list.contains(msgSource)) {
            return true;
        }

        // 过滤掉5分钟前的消息
        if (chatMessage.getCreateTime() - (System.currentTimeMillis() / 1000) > 60 * 5) {
            return true;
        }
        return false;


    }

    @Override
    public Boolean filterErrorMessage(String requestBody) {
        //gewechat服务发送的回调测试消息
        JSONObject response = JSONObject.parseObject(requestBody);
        if (response.containsKey("testMsg") && response.containsKey("token")) {
            return true;
        }
        // 过滤掉不包含Data字段的信息
        if (!response.containsKey("Data")) {
            return true;
        }
        if (!response.getJSONObject("Data").containsKey("NewMsgId")) {
            return true;
        }
        return false;
    }

    /**
     * 将类型修改为图片类型
     *
     * @param chatMessage
     */
    private void updateMsgType(ChatMessage chatMessage) {

        String content = chatMessage.getContent();
        List<String> imageCreatePrefix = botConfig.getImageCreatePrefix();
        for (String createPrefix : imageCreatePrefix) {
            if (content.contains(createPrefix)) {
                chatMessage.setCtype(MsgTypeEnum.IMAGE);
                return;
            }
        }
        if (content.contains("视频") && content.contains("生成")) {
            chatMessage.setCtype(MsgTypeEnum.VIDEO);
            return;
        }
        if (content.contains("语音") && content.contains("生成")) {
            chatMessage.setCtype(MsgTypeEnum.VOICE);
            return;
        }


    }


}
