package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.MessageService;
import com.wechat.bot.service.MsgSourceService;
import com.wechat.gewechat.service.ContactApi;
import com.wechat.gewechat.service.DownloadApi;
import com.wechat.util.ImageUtil;
import com.wechat.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alex
 * @since 2025/1/26 20:00
 * <p></p>
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    /**
     * 联系人map，用线程安全的map
     */
    private final Map<String, String> contactMap = new ConcurrentHashMap<>();

    @Resource
    private MsgSourceService msgSourceService;

    @Resource
    private BotConfig botConfig;

    @Override
    public void receiveMsg(JSONObject requestBody) {

        String appid = requestBody.getString("Appid");
        String wxid = requestBody.getString("Wxid");
        JSONObject data = requestBody.getJSONObject("Data");
        String fromUserId = data.getJSONObject("FromUserName").getString("string");
        String toUserId = data.getJSONObject("ToUserName").getString("string");
        String receiveMsg = data.getJSONObject("Content").getString("string");
        String msgSource = data.getString("MsgSource");
        String msgId = data.getString("NewMsgId");
        // 消息类型
        Integer msgType = data.getInteger("MsgType");
        ChatMessage chatMessage = ChatMessage.builder()
                .appId(appid)
                .msgId(msgId)
                .createTime(data.getLong("CreateTime"))
                .ctype(MsgTypeEnum.getMsgTypeEnum(msgType))
                .content(receiveMsg)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .isMyMsg(wxid.equals(fromUserId))
                .isGroup(fromUserId.contains("@chatroom"))
                .groupId(fromUserId)
                .isAt(receiveMsg.contains("@"))
                .groupMembersUserId(wxid)
                .rawMsg(requestBody)
                .build();

        // 过滤掉非用户信息
        if (filterNotUserMessage(chatMessage, msgSource)) {
            return;
        }
        // 消息内容进行处理
        messageContentProcessing(chatMessage);
        updateContactMaps(chatMessage);
        chatMessage.setFromUserNickname(contactMap.get(chatMessage.getFromUserId()));
        chatMessage.setToUserNickname(contactMap.get(chatMessage.getToUserId()));
        if (!chatMessage.getIsGroup()) {
            logMessage("收到个人消息：来自：{}，消息内容为：{}", chatMessage.getFromUserNickname(), chatMessage.getContent());
            msgSourceService.personalMsg(chatMessage);
            return;
        }
        // 群消息
        processGroupMessage(chatMessage);
        logMessage("群消息:《{}》中，{}的消息内容为：{}", chatMessage.getGroupIdNickName(), chatMessage.getGroupMemberUserNickname(), chatMessage.getContent());
        msgSourceService.groupMsg(chatMessage);
    }

    private void processGroupMessage(ChatMessage chatMessage) {

        String[] split = chatMessage.getContent().split(":");
        String content = split[1].replace('\n', ' ').strip();
        String groupMembersUserId = split[0];
        updateContactMap(groupMembersUserId);

        if (chatMessage.getIsAt()) {
            content = content.replace('@', ' ').strip();
            if (chatMessage.getContent().contains(chatMessage.getToUserNickname())) {
                content = content.replace(chatMessage.getToUserNickname(), "").strip();
            }
        }
        chatMessage.setContent(content);
        chatMessage.setGroupMembersUserId(groupMembersUserId);
        chatMessage.setGroupMemberUserNickname(contactMap.get(groupMembersUserId));
        chatMessage.setGroupIdNickName(contactMap.get(chatMessage.getGroupId()));
        // 将单人聊天这里面的值都设置为空，以此来彻底区分
        chatMessage.setFromUserId(null);
        chatMessage.setFromUserNickname(null);
    }

    private String parseGroupMessageContent(String content) {

        String[] split = content.split(":");
        return split[1].replace('\n', ' ').strip();
    }

    private void logMessage(String format, Object... args) {

        log.info(format, args);
    }


    private void setChatMessageType(ChatMessage chatMessage, MsgTypeEnum type) {

        chatMessage.setCtype(type);
    }

    private void updateContactMaps(ChatMessage chatMessage) {

        updateContactMap(chatMessage.getFromUserId());
        updateContactMap(chatMessage.getToUserId());
    }

    private void updateContactMap(String userId) {

        if (!contactMap.containsKey(userId)) {
            // 存到一个map里面不用每次都重新获取，降低请求次数
            // 获取好友的信息
            String nickName = getNickname(userId);
            contactMap.put(userId, nickName);
        }
    }

    private String getNickname(String userId) {

        JSONObject briefInfo = ContactApi.getBriefInfo(botConfig.getAppId(), Collections.singletonList(userId));
        if (briefInfo.getInteger("ret") == 200) {
            JSONArray dataList = briefInfo.getJSONArray("data");
            JSONObject userInfo = dataList.getJSONObject(0);
            String remark = userInfo.getString("remark");
            if (remark == null || remark.isBlank()) {
                return userInfo.getString("nickName");
            }
            return remark;
        }
        return null;
    }

    @Override
    public Map<String, String> getContactMap() {

        return this.contactMap;
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
        return chatMessage.getCreateTime() - (System.currentTimeMillis() / 1000) > 60 * 5;


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
        // 过滤掉不包含NewMsgId字段的信息
        return !response.getJSONObject("Data").containsKey("NewMsgId");
    }

    /**
     * 消息内容进行一系列的处理，处理逻辑比较复杂
     *
     * @param chatMessage
     */
    private void messageContentProcessing(ChatMessage chatMessage) {
        //判断消息类型，进行一系列的操作
        switch (chatMessage.getCtype()) {
            case TEXT:
                // 文本消息进行处理
                String content = chatMessage.getContent();
                //SessionManager sessionManager = msgSourceService.getSessionManager();
                //if (sessionManager == null) {
                //    return;
                //}
                //Session session = sessionManager.getSession(chatMessage.getFromUserId());
                //// 如果里面已经有了图片信息了，这里就不需要修改为图片了，防止两个触发逻辑混淆
                //if (session.getTextMessages().size() > 1) {
                //    return;
                //}
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
                break;
            case IMAGE:
                // 图片下载处理为base64位
                JSONObject jsonObject = DownloadApi.downloadImage(chatMessage.getAppId(), chatMessage.getContent(), 2);
                if (jsonObject.getInteger("ret") != 200) {
                    throw new RuntimeException("图片下载失败");
                }
                String imageStr = jsonObject.getJSONObject("data").getString("fileUrl");
                String imageUrl = "http://" + IpUtil.getIp() + ":2532/download/" + imageStr;
                Path imagePath = Path.of("data", "images", imageStr);
                imagePath.getParent().toFile().mkdirs();
                ImageUtil.downloadImage(imageUrl, imagePath.toString());
                // 图片下载可能会出现下载失败，而报错，请检查一下你的容器，容器内是否有问题
                chatMessage.setContent(imagePath.toAbsolutePath().toString());
                chatMessage.setCtype(MsgTypeEnum.IMAGERECOGNITION);
                break;
            case VOICE:
                break;
            case LINK:
                break;
            case VIDEO:
                break;
            default:
                break;

        }


    }

}
