package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.AiSystemRoleService;
import com.wechat.bot.service.MessageService;
import com.wechat.bot.service.MsgSourceService;
import com.wechat.bot.service.UserInfoService;
import com.wechat.gewechat.service.DownloadApi;
import com.wechat.gewechat.service.MessageApi;
import com.wechat.util.FileUtil;
import com.wechat.util.ImageUtil;
import com.wechat.util.IpUtil;
import com.wechat.util.WordParticipleMatch;
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
     * 语音模式或文本模式切换,默认是文本模式
     */
    private final Map<String, MsgTypeEnum> msgTypeEnumMap = new ConcurrentHashMap<>();

    @Resource
    UserInfoService userInfoService;

    @Resource
    private MsgSourceService msgSourceService;

    @Resource
    private BotConfig botConfig;

    @Resource
    private AiSystemRoleService aiSystemRoleService;

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
                .groupMembersUserId(wxid)
                .isAt(false)
                .rawMsg(requestBody)
                .build();

        // 过滤掉非用户信息
        if (filterNotUserMessage(chatMessage, msgSource)) {
            return;
        }


        if (isBotManual(chatMessage)) {
            return;
        }
        // 消息内容进行处理
        userInfoService.updateUserInfo(chatMessage);
        // 消息处理器
        if (this.contentProcessing(chatMessage)) {
            return;
        }
        if (!chatMessage.getIsGroup()) {
            logMessage("收到个人消息：来自：{}，消息内容为：{}", chatMessage.getFromUserNickname(), chatMessage.getContent());
            msgSourceService.personalMsg(chatMessage);
            return;
        }
        // 群消息内容处理
        //this.processGroupMessage(chatMessage);
        logMessage("群消息:《{}》中，{}的消息内容为：{}", chatMessage.getGroupIdNickName(), chatMessage.getGroupMemberUserNickname(), chatMessage.getContent());
        msgSourceService.groupMsg(chatMessage);
    }

    private void processGroupMessage(ChatMessage chatMessage) {

        String[] split = chatMessage.getContent().split(":");
        if (split.length < 2) {
            log.warn("Invalid group message format: {}", chatMessage.getContent());
            return;
        }
        String content = split[1].replace('\n', ' ').strip();
        String groupMembersUserId = split[0];
        userInfoService.updateUserInfo(groupMembersUserId);

        if (content.contains("@") && content.contains(chatMessage.getToUserNickname())) {
            content = content.replace('@', ' ').strip().replace(chatMessage.getToUserNickname(), "");
            chatMessage.setIsAt(true);
        }
        chatMessage.setContent(content);
        chatMessage.setGroupMembersUserId(groupMembersUserId);
        chatMessage.setGroupMemberUserNickname(userInfoService.getUserInfo().get(groupMembersUserId));
        chatMessage.setGroupIdNickName(userInfoService.getUserInfo().get(chatMessage.getGroupId()));

    }


    private void logMessage(String format, Object... args) {

        log.info(format, args);
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
        return chatMessage.getCreateTime() < (System.currentTimeMillis() / 1000 - 60 * 5);

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
     * content 内容处理器,如果需要接入AI就返回false，否则就返回true，直接返回
     *
     * @param chatMessage
     */
    private Boolean contentProcessing(ChatMessage chatMessage) {

        boolean isStop = false;
        //判断消息类型，进行一系列的操作
        switch (chatMessage.getCtype()) {
            case TEXT:
                isStop = textMessage(chatMessage);
                break;
            case IMAGE:
                // 图片下载处理为base64位
                if (!chatMessage.getIsGroup()) {
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
                }
                break;
            case VOICE:
            case LINK:
            case VIDEO:
            default:
                break;

        }
        return isStop;

    }

    /**
     * 机器人使用说明
     */
    private Boolean isBotManual(ChatMessage chatMessage) {

        boolean isContain = WordParticipleMatch.containsPartKeywords(chatMessage.getContent(), List.of("助理", "使用说明", "说明书"), 2);
        if (isContain) {
            String replay = FileUtil.readUseManual();
            MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), replay, chatMessage.getToUserId());
            return true;
        }
        return false;
    }

    /**
     * 文本消息，各种状态转变
     */
    public Boolean textMessage(ChatMessage chatMessage) {

        String userId = null;
        if (chatMessage.getIsGroup()) {
            // 群消息，这里需要处理群消息
            this.processGroupMessage(chatMessage);
            userId = chatMessage.getFromUserId().concat("-").concat(chatMessage.getGroupMembersUserId());
        } else {
            userId = chatMessage.getFromUserId();
        }
        String content = chatMessage.getContent();
        // 先判断是否是语音模式
        if (content.contains("语音模式")) {
            msgTypeEnumMap.put(userId, MsgTypeEnum.VOICE);
            MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), "语音模式已开启", chatMessage.getToUserId());
            return true;
        }
        // 在判断是否是图片模式
        if (content.contains("图片模式")) {
            msgTypeEnumMap.put(userId, MsgTypeEnum.IMAGE);
            MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), "图片模式已开启，发送想要生成的图片内容即可", chatMessage.getToUserId());
            return true;
        }
        //再次判断是否是视频模式
        if (content.contains("视频模式")) {
            msgTypeEnumMap.put(userId, MsgTypeEnum.VIDEO);
            MessageApi.postText(chatMessage.getAppId(), chatMessage.getFromUserId(), "视频模式已开启，描述一下想要生成的视频内容", chatMessage.getToUserId());
            return true;
        }
        // 如果上述都不是的话，那么就切换即可，需要留意是否有值

        //List<String> imageCreatePrefix = botConfig.getImageCreatePrefix();
        //boolean isImageType = WordParticipleMatch.containsPartKeywords(content, imageCreatePrefix, 2);
        ////画图，目前是强制写死，不然会冲突，必须包含画与图片两个关键字
        //if (isImageType) {
        //    // 第一次进来会来到这边，第二次，就会走map，所以需要判断一下，如果map里面有这个key，就直接走map里面对应的值
        //    chatMessage.setCtype(MsgTypeEnum.IMAGE);
        //    return;
        //}
        //boolean isVideoType = WordParticipleMatch.containsPartKeywords(content, List.of("视频", "生成"), 2);
        //if (isVideoType) {
        //    chatMessage.setCtype(MsgTypeEnum.VIDEO);
        //    return;
        //}
        //
        //
        //boolean containsPartKeywords = WordParticipleMatch.containsPartKeywords(content, List.of("关闭", "文字", "模式", "文本"), 2);
        //if (containsPartKeywords) {
        //    msgTypeEnumMap.remove(userId);
        //}
        MsgTypeEnum typeEnumMapOrDefault = msgTypeEnumMap.getOrDefault(userId, MsgTypeEnum.TEXT);
        chatMessage.setCtype(typeEnumMapOrDefault);
        return false;
    }

}
